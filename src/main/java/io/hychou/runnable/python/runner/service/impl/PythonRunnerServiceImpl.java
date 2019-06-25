package io.hychou.runnable.python.runner.service.impl;

import io.hychou.common.crosssystem.CrossSystemCommand;
import io.hychou.common.crosssystem.LinuxCommand;
import io.hychou.common.crosssystem.WindowsCommand;
import io.hychou.common.exception.server.OSNotSupportedException;
import io.hychou.common.exception.server.ServerException;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.utilities.IOUtilities;
import io.hychou.config.RunnablePathProperties;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.runner.profile.dao.PythonRunnerProfileRepository;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PythonRunnerServiceImpl implements PythonRunnerService {

    private final TaskExecutor mainTaskExecutor;
    private final TaskExecutor stdoutStreamGobblerExecutor;
    private final TaskExecutor stderrStreamGobblerExecutor;
    private final Logger logger = getLogger(this.getClass());
    private final PythonRunnerProfileRepository pythonRunnerProfileRepository;
    private final AnacondaYamlRepository anacondaYamlRepository;
    private final Path baseWorkingDir;
    private PythonRunnerProfileEntity pythonRunnerProfileEntity;

    public PythonRunnerServiceImpl(@Qualifier("threadPoolTaskExecutor") TaskExecutor mainTaskExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stdoutStreamGobblerExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stderrStreamGobblerExecutor,
                                   PythonRunnerProfileRepository pythonRunnerProfileRepository,
                                   AnacondaYamlRepository anacondaYamlRepository,
                                   RunnablePathProperties runnablePathProperties) {
        this.mainTaskExecutor = mainTaskExecutor;
        this.stdoutStreamGobblerExecutor = stdoutStreamGobblerExecutor;
        this.stderrStreamGobblerExecutor = stderrStreamGobblerExecutor;
        this.pythonRunnerProfileRepository = pythonRunnerProfileRepository;
        this.anacondaYamlRepository = anacondaYamlRepository;
        this.baseWorkingDir = Paths.get(runnablePathProperties.getBaseWorkingDir());
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
            } catch (ServiceException | ServerException | InterruptedException | IOException e) {
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
            IOUtilities.createDirectoryAndDeleteFirstIfPathIsFile(baseWorkingDir);
            absoluteWorkDirectory = Files.createTempDirectory(baseWorkingDir, null);
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

        private void runPythonCode() throws OSNotSupportedException, IOException, InterruptedException, ServiceException {
            logger.info("Start running python code");
            //TODO: add support for linux command
            ProcessBuilder builder = new CrossSystemCommand(
                    new LinuxCommand("sh", "-c", "ls"),
                    new WindowsCommand("cmd.exe", "/c",
                            String.format("%s && python %s",
                                    pythonRunnerProfileEntity.getEnvironment().getTimeVariantData().getCondaActivateCommand(),
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
            addResultFilesIntoPythonRunnerProfile(pythonRunnerProfileEntity);
        }

        private void addResultFilesIntoPythonRunnerProfile(PythonRunnerProfileEntity pythonRunnerProfileEntity) throws IOException {
            try {
                Set<FileEntity> fileEntities =
                        Files.find(this.absoluteWorkDirectory, Integer.MAX_VALUE,
                                (filePath, fileAttr) -> fileAttr.isRegularFile()).map((f) -> {
                            Path relativePath = this.absoluteWorkDirectory.relativize(f);
                            logger.info("adding file \"{}\" into result", relativePath.toString());
                            // default lambda function does not support throws,
                            // a way to overpass it is to throw an uncheck exception
                            // and then catch it outside the lambda.
                            // We use a lambda instead of traditional for-looping a list
                            // as we must save it to a list and then for-looping it.
                            try {
                                return new FileEntity(relativePath.toString(),
                                        IOUtils.toByteArray(f.toUri()));
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }).collect(Collectors.toSet());
                pythonRunnerProfileEntity.setResult(fileEntities);
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        private void cleanEnvironment() throws IOException {
            FileUtils.deleteDirectory(this.absoluteWorkDirectory.toFile());
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
