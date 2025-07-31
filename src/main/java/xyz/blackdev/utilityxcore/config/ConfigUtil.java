package xyz.blackdev.utilityxcore.config;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigUtil {
    private final Path configFilePath;
    private final Properties properties = new Properties();

    public ConfigUtil(Path configFilePath) {
        this.configFilePath = configFilePath;
        loadConfig();
    }

    private void loadConfig() {
        if (Files.exists(configFilePath)) {
            try (InputStream in = Files.newInputStream(configFilePath)) {
                properties.load(in);
            } catch (IOException e) {
                System.err.println("Error loading " + configFilePath + ": " + e.getMessage());
            }
        }
    }

    public void saveConfig() {
        try (OutputStream out = Files.newOutputStream(configFilePath)) {
            properties.store(out, "Saved config");
        } catch (IOException e) {
            System.err.println("Error saving " + configFilePath + ": " + e.getMessage());
        }
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void remove(String key) {
        properties.remove(key);
    }

    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    public void list() {
        properties.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}
