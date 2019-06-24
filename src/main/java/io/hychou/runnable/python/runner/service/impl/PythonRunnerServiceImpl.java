package io.hychou.runnable.python.runner.service.impl;

import io.hychou.common.crosssystem.CrossSystemCommand;
import io.hychou.common.crosssystem.LinuxCommand;
import io.hychou.common.crosssystem.WindowsCommand;
import io.hychou.common.exception.server.OSNotSupportedException;
import io.hychou.common.exception.server.ServerException;
import io.hychou.common.utilities.IOUtilities;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.runner.profile.dao.PythonRunnerProfileRepository;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PythonRunnerServiceImpl implements PythonRunnerService {

    private final TaskExecutor mainTaskExecutor;
    private final TaskExecutor stdoutStreamGobblerExecutor;
    private final TaskExecutor stderrStreamGobblerExecutor;
    private final Logger logger = getLogger(this.getClass());
    private final PythonRunnerProfileRepository pythonRunnerProfileRepository;
    private final AnacondaYamlRepository anacondaYamlRepository;
    private PythonRunnerProfileEntity pythonRunnerProfileEntity;

    @Value("${path.runnable.base-dir}")
    private String runnableBaseDirectoryString;

    public PythonRunnerServiceImpl(@Qualifier("threadPoolTaskExecutor") TaskExecutor mainTaskExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stdoutStreamGobblerExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stderrStreamGobblerExecutor,
                                   PythonRunnerProfileRepository pythonRunnerProfileRepository,
                                   AnacondaYamlRepository anacondaYamlRepository) {
        this.mainTaskExecutor = mainTaskExecutor;
        this.stdoutStreamGobblerExecutor = stdoutStreamGobblerExecutor;
        this.stderrStreamGobblerExecutor = stderrStreamGobblerExecutor;
        this.pythonRunnerProfileRepository = pythonRunnerProfileRepository;
        this.anacondaYamlRepository = anacondaYamlRepository;
    }

    public void run(PythonRunnerProfileEntity pythonRunnerProfileEntity) {
        this.pythonRunnerProfileEntity = pythonRunnerProfileEntity;
        this.mainTaskExecutor.execute(new PythonRunnable());
    }

    private class PythonRunnable implements Runnable {

        private Path absoluteWorkDirectory;

        @Override
        public void run() {
            try {
                pythonRunnerProfileEntity.toPreparingState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
                prepareEnvironment();
                pythonRunnerProfileEntity.toRunningState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
                runPythonCode();
                pythonRunnerProfileEntity.toCleaningState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
                cleanEnvironment();
                pythonRunnerProfileEntity.toFinishedState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
            } catch (ServerException | InterruptedException | IOException e) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                pythonRunnerProfileEntity.addErrorMessage(stringWriter.toString());
                pythonRunnerProfileEntity.toCrashedState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
            } catch (Exception e) {
                pythonRunnerProfileEntity.toCrashedState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                pythonRunnerProfileEntity.addErrorMessage(stringWriter.toString());
                throw e;
            }
        }

        private void prepareEnvironment() throws IOException, InterruptedException, ServerException {
            logger.info("Start preparing environment");
            Path runnableBaseDirectory = Paths.get(runnableBaseDirectoryString);
            IOUtilities.createDirectory(runnableBaseDirectory, "base runnable");
            absoluteWorkDirectory = Files.createTempDirectory(runnableBaseDirectory, null);
            logger.info("The created temp directory: {}", absoluteWorkDirectory);
            absoluteWorkDirectory.toFile().deleteOnExit();
            pythonRunnerProfileEntity.getPythonCode().getTimeVariantData().writeToFile(absoluteWorkDirectory);
            AnacondaYamlEntity anacondaYamlEntity = pythonRunnerProfileEntity.getEnvironment().getTimeVariantData();
            anacondaYamlEntity.prepareAnacondaEnvironment();
            anacondaYamlRepository.save(anacondaYamlEntity);
            for (TimeDependentEntity<FileEntity> dependency : pythonRunnerProfileEntity.getDependencies()) {
                dependency.getTimeVariantData().writeToFile(this.absoluteWorkDirectory);
            }
        }

        private void runPythonCode() throws OSNotSupportedException, IOException, InterruptedException {
            logger.info("Start running python code");
            //TODO: add support for linux command
            ProcessBuilder builder = new CrossSystemCommand(
                    new LinuxCommand("sh", "-c", "ls"),
                    new WindowsCommand("cmd.exe", "/c",
                            String.format("%s && python %s",
                                    pythonRunnerProfileEntity.getEnvironment().getTimeVariantData().getCondaActivateCommand(),
//                                    pythonRunnerProfileEntity.getEnvironment().getTimeVariantData().getEnvironmentPath(),
                                    pythonRunnerProfileEntity.getPythonCode().getTimeVariantData().getName()))
            ).build();

            builder.directory(absoluteWorkDirectory.toFile());
            logger.info("start process with command: \"{}\"", builder.command());
            Process process = builder.start();
            StringJoiner stdoutJoiner = new StringJoiner(System.lineSeparator());
            StreamGobbler stdoutStreamGobbler = new StreamGobbler(process.getInputStream(), stdoutJoiner::add);
            stdoutStreamGobblerExecutor.execute(stdoutStreamGobbler);
            List<String> stderrList = new ArrayList<>();
            StreamGobbler stderrStreamGobbler = new StreamGobbler(process.getErrorStream(), (err) -> stderrList.add(String.format("%s: %s", new Date().toString(), err)));
            stderrStreamGobblerExecutor.execute(stderrStreamGobbler);

            if (process.waitFor() != 0) {
                pythonRunnerProfileEntity.addErrorMessage(String.format("Process exit with error code: %d", process.exitValue()));
            }

            for (String stderr : stderrList) {
                pythonRunnerProfileEntity.addErrorMessage(stderr);
            }
            pythonRunnerProfileEntity.setSummary(stdoutJoiner.toString());
        }

        private void cleanEnvironment() throws IOException {
//            FileUtils.deleteDirectory(this.absoluteWorkDirectory.toFile());
        }

        private class StreamGobbler implements Runnable {
            private InputStream inputStream;
            private Consumer<String> consumer;

            StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
                this.inputStream = inputStream;
                this.consumer = consumer;
            }

            @Override
            public void run() {
                new BufferedReader(new InputStreamReader(inputStream)).lines()
                        .forEach(consumer);
            }
        }
    }
}
