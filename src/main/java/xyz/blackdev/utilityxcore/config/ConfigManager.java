package xyz.blackdev.utilityxcore.config;

import com.google.gson.JsonElement;
import de.craftsblock.craftscore.json.Json;
import de.craftsblock.craftscore.json.JsonParser;
import xyz.blackdev.utilityxcore.UtilityXCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    public Json createConfig(String name, Path dir) {
        return createConfig(dir.resolve(name), dir);
    }

    public Json createConfig(Path file, Path dir) {

        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory: " + dir, e);
        }

        // Normalize the filename to end with .json (without recursion)
        String filename = file.getFileName().toString();
        if (!filename.endsWith(".json")) {
            file = file.resolveSibling(filename + ".json");
        }


        Path parent = file.getParent();
        if (parent != null) {
            try {
                if (Files.notExists(parent)) {
                    Files.createDirectories(parent);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create parent directory: " + parent, e);
            }
        }


        try {
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config file: " + file, e);
        }

        return JsonParser.parse(file);
    }

    public Json createConfig(Path file) {
        Path dir = Paths.get(UtilityXCore.getInstance().getDataPath().toString(), "configs");

        if (!file.isAbsolute() && file.getParent() == null) {
            file = dir.resolve(file);
        }
        return createConfig(file, dir);
    }

    public void write(String name, Path jsonFile, Object obj) {
        write(name, fromFile(jsonFile), obj);
    }

    public void write(String name, Json json, Object obj) {
        json.set(name, obj);

    }

    public JsonElement get(String name, Path jsonFile) {
        return get(name, fromFile(jsonFile));
    }

    public JsonElement get(String name, Json json) {
        return json.get(name);
    }

    public Json fromFile(Path file) {
        return JsonParser.parse(file);
    }
}
