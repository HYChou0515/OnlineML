package io.hychou.common.utilities;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

public class IOUtilities {

    private final static Logger logger = getLogger(IOUtilities.class);

    public static void createDirectoryAndDeleteFirstIfPathIsFile(Path directoryPath) throws IOException {
        logger.info("Creating temp directory at: {}", directoryPath.toString());
        if (!directoryPath.toFile().isDirectory() && !directoryPath.toFile().mkdirs()) {
            throw new IOException("Cannot create temp directory at: {}" + directoryPath.toString());
        }
    }
}
