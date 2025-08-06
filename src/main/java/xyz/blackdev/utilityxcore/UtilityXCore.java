package xyz.blackdev.utilityxcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.blackdev.utilityxcore.addon.Addon;
import xyz.blackdev.utilityxcore.addon.AddonListener;
import xyz.blackdev.utilityxcore.commands.AddonCommand;
import xyz.blackdev.utilityxcore.commands.CheckVersionCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCoreCommand;
import xyz.blackdev.utilityxcore.handlers.ConfigHandler;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;
import xyz.xenondevs.invui.InvUI;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {
    public static Logger logger = Logger.getLogger("UX-Core");
    public static String version = "060825";

    private static UtilityXCore instance;
    @Override
    public void onEnable() {
        UtilityXCore.instance = this;

        logger.info("UtilityXCore has been enabled!");
        DirectoryHandler.createDirectories();
        ConfigHandler.createConfigs();
        InvUI.getInstance().setPlugin(this);

        getServer().getPluginManager().registerEvents(new AddonListener(), this);
        getCommand("utilityx").setExecutor(new UtilityXCommand());
        getCommand("addon").setExecutor(new AddonCommand());
        getCommand("UXVersion").setExecutor(new CheckVersionCommand());
        getCommand("UtilityXCore").setExecutor(new UtilityXCoreCommand());

        Bukkit.getScheduler().runTask(this, this::loadAddons);
    }

    public void loadAddons() {
        try {
            Files.walk(DirectoryHandler.getAddons(), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Addon addon = Addon.fetchDataLocally(file);
                            if (addon != null) {
                                addon.load();
                            } else {
                                System.err.println("Failed to load addon from: " + file);
                            }
                        } catch (Exception ex) {
                            System.err.println("Error loading addon from: " + file);
                            ex.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error walking module path: " + DirectoryHandler.getAddons());
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
}
