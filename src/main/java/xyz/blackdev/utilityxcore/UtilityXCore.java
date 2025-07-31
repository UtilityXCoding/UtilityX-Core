package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.blackdev.utilityxcore.config.ConfigManager;
import xyz.blackdev.utilityxcore.handlers.ConfigHandler;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;
import xyz.blackdev.utilityxcore.utils.DirectoryUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    public static Logger logger = Logger.getLogger("UX-Core");
    public static ConfigManager manager;

    @Override
    public void onEnable() {
        logger.info("UtilityXCore has been enabled!");
        manager = new ConfigManager(getServer().getWorldContainer().getAbsolutePath() + "/plugins/UX/UXConfigs");
        DirectoryHandler.CreateDirectories();
        ConfigHandler.CreateConfigs();
    }

    @Override
    public void onDisable() {
    manager.saveAll();
    logger.info("UtilityXCore has been disabled!");



    }
}
