package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    public static Logger logger = Logger.getLogger("UXCore");
    @Override
    public void onEnable() {
        logger.info("UtilityXCore has been enabled!");


    }

    @Override
    public void onDisable() {

    logger.info("UtilityXCore has been disabled!");

    }
}
