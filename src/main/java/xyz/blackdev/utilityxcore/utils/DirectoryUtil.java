package xyz.blackdev.utilityxcore.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class DirectoryUtil {

    public static void ensureDirectoriesExist(Logger logger, String basePath, String... relativeDirs) {
        for (String dir : relativeDirs) {
            Path path = Path.of(basePath, dir);
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                    logger.info("Created directory: " + path);
                } else {
                    logger.info("Directory already exists: " + path);
                }
            } catch (Exception e) {
                logger.severe("Failed to create directory " + path + ": " + e.getMessage());
            }
        }
    }

    public static Path createUXSubDirectory(Logger logger, String basePath, String folderName) {
        Path path = Path.of(basePath, "UX", folderName);
        try {
            if (!Files.exists(path)) {
                logger.info("Created directory: " + path);
                return Files.createDirectories(path);
            } else {
                logger.info("Directory already exists: " + path);
                return path;
            }
        } catch (Exception e) {
            logger.severe("Failed to create directory " + path + ": " + e.getMessage());
        }
        return null;
    }
}