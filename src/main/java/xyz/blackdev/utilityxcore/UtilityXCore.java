package xyz.blackdev.utilityxcore;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.blackdev.utilityxcore.commands.CheckVersionCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCoreCommand;
import xyz.blackdev.utilityxcore.handlers.ConfigHandler;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;
import xyz.xenondevs.invui.InvUI;

import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    public static Logger logger = Logger.getLogger("UX-Core");
    public static String version = "010825";

    @Override
    public void onEnable() {
        logger.info("UtilityXCore has been enabled!");
        DirectoryHandler.CreateDirectories();
        ConfigHandler.CreateConfigs();
        InvUI.getInstance().setPlugin(this);

        getCommand("utilityx").setExecutor(new UtilityXCommand());
        getCommand("UXVersion").setExecutor(new CheckVersionCommand());
        getCommand("UtilityXCore").setExecutor(new UtilityXCoreCommand());
    }

    @Override
    public void onDisable() {
    logger.info("UtilityXCore has been disabled!");
    }
}
