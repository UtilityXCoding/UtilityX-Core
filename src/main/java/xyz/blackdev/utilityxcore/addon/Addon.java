package xyz.blackdev.utilityxcore.addon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.utils.DownloadUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class Addon {
    private final Path file;

    //private Addon(String name, String newestVersion, Path file) {
    //    this.name = name;
    //    this.newestVersion = newestVersion;
    //    this.file = file;
    //}

    private Addon(Path file) {
        this.file = file;
    }

    public static Addon fetchDataFromURL(String url) {
        try {
            DownloadUtil.downloadFile(url, UtilityXCore.getInstance().getModulePath().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    //public static Addon fetchDataLocally(String name, String newestVersion) {
    //    Path file = UtilityXCore.getInstance().getModulePath().resolve(name.endsWith(".jar") ? name : name + ".jar");

    //    if(Files.notExists(file)) return null;

    //    return new Addon(name, newestVersion, file);
    //}

    public static Addon fetchDataLocally(Path file ) {
        if(Files.notExists(file)) return null;

        return new Addon(file);
    }

    public void load() {
        try {
            Bukkit.getPluginManager().loadPlugin(file.toFile());
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            throw new RuntimeException(e);
        }
    }
}
