package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.blackdev.utilityxcore.addon.Addon;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;


// config setting
public final class UtilityXCore extends JavaPlugin {
    public static Logger logger = Logger.getLogger("UXCore");
    private Path modulePath;
    private static UtilityXCore instance;
    @Override
    public void onEnable() {
        instance = this;

        String modulePathString;

        if(!this.getConfig().contains("module-path")) this.getConfig().set("module-path", "modules");

        modulePathString = this.getConfig().getString("module-path");

        if (modulePathString != null) {
            this.getConfig().set("module-path", "modules"); //technically not needed, but just to be sure that i dont  break shit

            modulePath = this.getDataPath().resolve(modulePathString);
        }

        loadAddons();

        logger.info("UtilityXCore has been enabled!");
    }

    public void loadAddons() {
        try {
            Files.walk(modulePath, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Addon addon = Addon.fetchDataLocally(file);
                            if (addon != null) {
                                addon.load();
                            } else {
                                System.err.println("Failed to load addon: " + file);
                            }
                        } catch (Exception ex) {
                            System.err.println("Error loading addon: " + file);
                            ex.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error walking module path: " + modulePath);
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        logger.info("UtilityXCore has been disabled!");
    }

    public static UtilityXCore getInstance() {
        return instance;
    }

    public Path getModulePath() {
        return modulePath;
    }
}
