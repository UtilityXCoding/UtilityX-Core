package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    public static Logger logger = Logger.getLogger("UX-Core");
    public Path uxAddonsDir = Path.of(getServer().getWorldContainer().getAbsolutePath(), "plugins/UXAddons");

    @Override
    public void onEnable() {
        logger.info("UtilityXCore has been enabled!");
        try {
            if (!Files.exists(uxAddonsDir)) {
                Files.createDirectories(uxAddonsDir);
                logger.info("Created UXAddons directory at: " + uxAddonsDir);
            } else {
                logger.info("UXAddons directory already exists at: " + uxAddonsDir);
            }
        } catch (Exception e) {
            logger.severe("Failed to create UXAddons directory: " + e.getMessage());
        }

    }

    @Override
    public void onDisable() {

    logger.info("UtilityXCore has been disabled!");

    }
}
