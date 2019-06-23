package io.hychou.runnable.python.runner.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.servererror.OSNotSupportedException;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.runner.profile.dao.PythonRunnerProfileRepository;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private PythonRunnerProfileEntity pythonRunnerProfileEntity;
    private PythonRunnerProfileRepository pythonRunnerProfileRepository;

    public PythonRunnerServiceImpl(@Qualifier("threadPoolTaskExecutor") TaskExecutor mainTaskExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stdoutStreamGobblerExecutor,
                                   @Qualifier("threadPoolTaskExecutor") TaskExecutor stderrStreamGobblerExecutor) {
        this.mainTaskExecutor = mainTaskExecutor;
        this.stdoutStreamGobblerExecutor = stdoutStreamGobblerExecutor;
        this.stderrStreamGobblerExecutor = stderrStreamGobblerExecutor;
    }

    public void run(PythonRunnerProfileEntity pythonRunnerProfileEntity, PythonRunnerProfileRepository pythonRunnerProfileRepository) {
        this.pythonRunnerProfileEntity = pythonRunnerProfileEntity;
        this.pythonRunnerProfileRepository = pythonRunnerProfileRepository;
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
            } catch (ServiceException e) {
                pythonRunnerProfileEntity.addErrorMessage(e.getMessage());
                pythonRunnerProfileEntity.toFinishedState();
                pythonRunnerProfileEntity = pythonRunnerProfileRepository.save(pythonRunnerProfileEntity);
            } catch (InterruptedException | IOException e) {
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

        private void prepareEnvironment() throws IOException {
            absoluteWorkDirectory = Files.createTempDirectory(null);
            absoluteWorkDirectory.toFile().deleteOnExit();
            pythonRunnerProfileEntity.getPythonCode().getTimeVariantData().writeToFile(absoluteWorkDirectory);
            pythonRunnerProfileEntity.getEnvironment().getTimeVariantData().prepareEnvironment(absoluteWorkDirectory);
            for (TimeDependentEntity<FileEntity> dependency : pythonRunnerProfileEntity.getDependencies()) {
                dependency.getTimeVariantData().writeToFile(this.absoluteWorkDirectory);
            }
        }

        private void runPythonCode() throws ServiceException, IOException, InterruptedException {
            ProcessBuilder builder = new ProcessBuilder();
            if (SystemUtils.IS_OS_LINUX) {
                builder.command("sh", "-c", "ls");
            } else if (SystemUtils.IS_OS_WINDOWS) {
                builder.command("cmd.exe", "/c", "python", pythonRunnerProfileEntity.getPythonCode().getTimeVariantData().getName());
            } else {
                throw new OSNotSupportedException("Server Operating System is not supported");
            }
            builder.directory(absoluteWorkDirectory.toFile());
            logger.info("run command: " + StringUtils.repeat("{} ", builder.command().size()), builder.command().toArray());
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
