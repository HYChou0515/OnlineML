package io.hychou.common.utilities;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class IOUtilities {

    private final static Logger logger = getLogger(IOUtilities.class);

    public static void createDirectory(Path directoryPath, String metaname) throws IOException {
        logger.info("Creating {} as temp directory at: {}", metaname, directoryPath.toString());
        if (!directoryPath.toFile().isDirectory() && !directoryPath.toFile().mkdirs()) {
            throw new IOException("Cannot create " + metaname + " as temp directory: " + directoryPath.toString());
        }
    }

    public static void createDirectory(String directoryPath, String metaname) throws IOException {
        createDirectory(Paths.get(directoryPath), metaname);
    }
}
