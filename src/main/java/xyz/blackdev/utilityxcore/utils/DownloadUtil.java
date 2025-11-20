package xyz.blackdev.utilityxcore.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadUtil {
    public static void downloadFile(String fileURL, String targetDir) throws Exception {
        URL url = new URL(fileURL);
        String fileName = Paths.get(url.getPath()).getFileName().toString();
        Path targetPath = Paths.get(targetDir, fileName);

        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}