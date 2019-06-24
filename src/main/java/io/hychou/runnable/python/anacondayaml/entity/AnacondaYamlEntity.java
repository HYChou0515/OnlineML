package io.hychou.runnable.python.anacondayaml.entity;

import io.hychou.common.crosssystem.CrossSystemCommand;
import io.hychou.common.crosssystem.LinuxCommand;
import io.hychou.common.crosssystem.WindowsCommand;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.exception.server.OSNotSupportedException;
import io.hychou.common.exception.server.ServerException;
import io.hychou.common.utilities.IOUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import javax.persistence.Entity;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static io.hychou.common.crosssystem.CrossSystemCommand.LINUX_DUMMY_COMMAND;
import static io.hychou.common.crosssystem.CrossSystemCommand.WINDOWS_DUMMY_COMMAND;
import static org.slf4j.LoggerFactory.getLogger;

@Entity
public class AnacondaYamlEntity extends BlobEntity {
    private final static Logger logger = getLogger(AnacondaYamlEntity.class);
    public static AnacondaYamlEntity DEFAULT_ANACONDA_YAML_ENTITY = new AnacondaYamlEntity("default_anaconda_yaml", new byte[0]);
    //    @Value("${java.io.tmpdir}/onlineml/env/")
    //TODO: make anacondaEnvBaseDir from properties
    private String anacondaEnvBaseDir = "C:\\Users\\Vito\\AppData\\Local\\Temp\\onlineml\\env";

    public AnacondaYamlEntity() {
        super();
    }

    public AnacondaYamlEntity(String name, byte[] fileBytes) {
        super(name, fileBytes);
    }

    public Path getEnvironmentPath() {
        return Paths.get(anacondaEnvBaseDir, getId().toString());
    }

    public String getCondaActivateCommand() throws OSNotSupportedException {
        // TODO: add support for linux command
        List<String> commandTokens;
        if (getBlobBytes().length == 0) {
            commandTokens = new CrossSystemCommand(
                    LINUX_DUMMY_COMMAND,
                    WINDOWS_DUMMY_COMMAND
            ).build().command();
        } else {
            // TODO: add support for linux command
            commandTokens = new CrossSystemCommand(
                    new LinuxCommand("conda", "deactivate"),
                    new WindowsCommand("activate", getEnvironmentPath().toString())
            ).build().command();
        }
        return String.join(" ", commandTokens);
    }

    public void prepareAnacondaEnvironment() throws IOException, InterruptedException, ServerException {
        if (getBlobBytes().length == 0) {
            logger.info("use default environment");
            return;
        }
        logger.info("start preparing anaconda environment in base dir: \"{}\"", anacondaEnvBaseDir);
        Path environmentBaseDirectory = Paths.get(anacondaEnvBaseDir);
        IOUtilities.createDirectory(environmentBaseDirectory, "base env");
        File environmentDir = getEnvironmentPath().toFile();
        // if the environment path is not a directory, it is not a valid environment path
        if (environmentDir.isDirectory()) {
            if (isAnacondaEnvironmentDir(environmentDir)) {
                return;
            }
            FileUtils.deleteDirectory(environmentDir);
        } else {
            logger.info("{} is not directory", getEnvironmentPath().toString());
            // the path is not directory
            if (environmentDir.exists() && !environmentDir.delete()) {
                // if the path is a file and cannot be deleted
                throw new IOException("Cannot delete the file: " + environmentDir.getAbsolutePath());
            }
        }
        // here the environment dir is deleted or not exist at first
        createAnacondaEnvironmentDir(environmentDir);
    }

    private void createAnacondaEnvironmentDir(File environmentDir) throws InterruptedException, IOException, ServerException {
        logger.info("start creating anaconda environment directory at: {}", environmentDir.getAbsolutePath());

        Path tempYamlFile = Files.createTempFile(Paths.get(anacondaEnvBaseDir), null, ".yml");
        logger.info("write temp yaml file as {}", tempYamlFile.toString());
        FileUtils.writeByteArrayToFile(tempYamlFile.toFile(), getBlobBytes());
        // TODO: add support for linux command
        new CrossSystemCommand(
                new LinuxCommand("sh", "-c", "ls"),
                new WindowsCommand("cmd.exe", "/c", "conda", "env", "create", "-p", getEnvironmentPath().toString(), "-f", tempYamlFile.toString())
        ).startAndWait();

        // export environment dir back to anaconda yaml
        logger.info("exporting environment dir as yaml back to entity blob");
        this.setBlobBytes(getAnacondaEnvironmentYamlByteArray(environmentDir));

        // at last check if the dir is created successfully
        if (!isAnacondaEnvironmentDir(environmentDir)) {
            throw new IOException(String.format("Failed to create anaconda environment directory at \"%s\"", environmentDir.getAbsolutePath()));
        }
    }

    private boolean isAnacondaEnvironmentDir(File environmentDir) throws ServerException, IOException, InterruptedException {
        //TODO: add support for linux command
        logger.info("checking if the environment is in conda list");
        Process process = new CrossSystemCommand(
                new LinuxCommand("sh", "-c", "ls"),
                new WindowsCommand("cmd.exe", "/c", "conda", "env", "list")
        ).startAndWait();
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        boolean isContainedInEnvList = false;
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            if (line.contains(environmentDir.getAbsolutePath())) {
                isContainedInEnvList = true;
            }
        }
        if (!isContainedInEnvList) return false;
        logger.info("the environment is in conda list");
        return isAnacondaEnvironmentTheSame(environmentDir);

    }

    private byte[] getAnacondaEnvironmentYamlByteArray(File environmentDir) throws ServerException, IOException, InterruptedException {
        //TODO: add support for linux command
        Process process = new CrossSystemCommand(
                new LinuxCommand("sh", "-c", "ls"),
                new WindowsCommand("cmd.exe", "/c", "conda", "env", "export", "-p", environmentDir.getAbsolutePath())
        ).startAndWait();
        return IOUtils.toByteArray(process.getInputStream());
    }

    private boolean isAnacondaEnvironmentTheSame(File environmentDir) throws ServerException, IOException, InterruptedException {
        byte[] anacondaEnvironmentYamlByteArray = getAnacondaEnvironmentYamlByteArray(environmentDir);
        logger.info("Comparing anaconda yaml:");
        logger.info("in entity: " + System.lineSeparator() + "{}", new String(getBlobBytes()));
        logger.info("in env dir" + System.lineSeparator() + "{}", new String(anacondaEnvironmentYamlByteArray));
        logger.info("result: {}", Arrays.equals(getBlobBytes(), anacondaEnvironmentYamlByteArray));
        return Arrays.equals(getBlobBytes(), anacondaEnvironmentYamlByteArray);
    }
}
