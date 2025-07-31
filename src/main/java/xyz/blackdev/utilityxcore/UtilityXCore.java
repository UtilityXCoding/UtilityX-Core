package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.blackdev.utilityxcore.handlers.ConfigHandler;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;
import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    public static Logger logger = Logger.getLogger("UX-Core");

    @Override
    public void onEnable() {
        logger.info("UtilityXCore has been enabled!");
        DirectoryHandler.CreateDirectories();
        ConfigHandler.CreateConfigs();
    }

    @Override
    public void onDisable() {
    logger.info("UtilityXCore has been disabled!");
    }
}
