package xyz.blackdev.utilityxcore.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.craftsblock.craftscore.json.Json;
import de.craftsblock.craftscore.json.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    public Json createConfig(String name, Path dir) {
        return createConfig(dir.resolve(name), dir);
    }

    public Json createConfig(Path file, Path dir) {
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(Files.notExists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return JsonParser.parse(file);
    }
}
