package xyz.blackdev.utilityxcore.addon;

import org.bukkit.Bukkit;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;
import xyz.blackdev.utilityxcore.utils.DirectoryUtil;
import xyz.blackdev.utilityxcore.utils.DownloadUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Addon {

    private static Set<Addon> addons = new HashSet<>();
    private final Path file;
    Plugin plugin;

    //private Addon(String name, String newestVersion, Path file) {
    //    this.name = name;
    //    this.newestVersion = newestVersion;
    //    this.file = file;
    //}

    private Addon(Path file) {
        this.file = file;

        addons.remove(this);
        addons.add(this);
    }
    public String getName() {
        //hier aus der config getten, ABER nur wenn in der config registriert sonst eif fallback auf den Filename

        return this.file.getFileName().toString();
    }
    /*
    * KP was ich hier vorhatte aber lass ma mal drinne xD
    * */
    public static @Nullable Addon fetchDataFromURL(String url) {
        try {
            if(DirectoryHandler.getAddons() != null) DownloadUtil.downloadFile(url, DirectoryHandler.getAddons().toString());
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
    public static @Nullable Addon fetchDataLocally(Path file ) {
        if(Files.notExists(file)) return null;

        return new Addon(file);
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public void load() {
        try {
            addons.remove(this);
            addons.add(this);
            this.plugin = Bukkit.getPluginManager().loadPlugin(file.toFile());
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean unload() {
        addons.remove(this); //falls es noch registriert sein sollte

        if(plugin == null) return false;

        Bukkit.getPluginManager().disablePlugin(this.plugin);

        return true;
    }
    public void reload() {
        unload();

        Bukkit.getScheduler().runTask(this.plugin, this::load);
    }
    public static Set<Addon> getAddons() {
        return addons;
    }
}
