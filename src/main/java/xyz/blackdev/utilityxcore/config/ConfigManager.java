package xyz.blackdev.utilityxcore.config;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final Map<String, ConfigUtil> configMap = new HashMap<>();
    private final Path configDirectory;

    public ConfigManager(String configDirectoryPath) {
        this.configDirectory = Paths.get(configDirectoryPath);
        loadAllConfigs();
    }

    private void loadAllConfigs() {
        try {
            if (!Files.exists(configDirectory)) {
                Files.createDirectories(configDirectory);
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(configDirectory, "*.ux")) {
                for (Path file : stream) {
                    String configName = stripSuffix(file.getFileName().toString());
                    configMap.put(configName, new ConfigUtil(file));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load configs from " + configDirectory + ": " + e.getMessage());
        }
    }

    public ConfigUtil getConfig(String name) {
        return configMap.computeIfAbsent(name, key -> {
            Path file = configDirectory.resolve(name + ".ux");
            return new ConfigUtil(file);
        });
    }

    public void saveAll() {
        configMap.values().forEach(ConfigUtil::saveConfig);
    }

    public void listAllConfigs() {
        configMap.forEach((name, config) -> {
            System.out.println("=== Config: " + name + " ===");
            config.list();
            System.out.println();
        });
    }

    private String stripSuffix(String fileName) {
        return fileName.endsWith(".ux") ? fileName.substring(0, fileName.length() - 3) : fileName;
    }
}
