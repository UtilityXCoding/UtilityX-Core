package xyz.blackdev.utilityxcore;

import de.utilityx.api.AddonRegistry;
import de.utilityx.api.ConfigFacade;
import de.utilityx.api.Directories;
import de.utilityx.api.UtilityX;
import de.utilityx.api.UtilityXAPI;
import de.utilityx.api.VersionInfoAddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.blackdev.utilityxcore.addon.Addon;
import xyz.blackdev.utilityxcore.addon.AddonListener;

import xyz.blackdev.utilityxcore.commands.AddonCommand;
import xyz.blackdev.utilityxcore.commands.CheckVersionCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCommand;
import xyz.blackdev.utilityxcore.commands.UtilityXCoreCommand;

import xyz.blackdev.utilityxcore.config.ConfigManager;
import xyz.blackdev.utilityxcore.handlers.ConfigHandler;
import xyz.blackdev.utilityxcore.handlers.DirectoryHandler;

import xyz.blackdev.utilityxcore.impl.BukkitAddonRegistry;
import xyz.blackdev.utilityxcore.impl.BukkitConfigFacade;
import xyz.blackdev.utilityxcore.impl.BukkitDirectories;

import xyz.xenondevs.invui.InvUI;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class UtilityXCore extends JavaPlugin {

    // ========= STATIC =========
    private static UtilityXCore instance;
    public static Logger logger = Logger.getLogger("UX-Core");
    public static String version = "210825";

    // ========= SYSTEMS =========
    public static ConfigManager configManager;
    private AddonRegistry addonRegistry;
    private Directories directories;
    private ConfigFacade configFacade;
    private VersionInfoAddon versionInfo;


    @Override
    public void onEnable() {

        // ========= INSTANCE =========
        instance = this;
        logger.info("UtilityXCore has been enabled!");

        // ========= CONFIG SYSTEM =========
        configManager = new ConfigManager();
        ConfigHandler.createConfigs();

        // ========= DIRECTORIES =========
        DirectoryHandler.createDirectories();
        Path addonsDir = DirectoryHandler.getAddons();

        this.directories = new BukkitDirectories(
                getServer().getWorldContainer().toPath(), // Server root
                getDataFolder().toPath(),                // Plugin data folder
                addonsDir,                               // Addon directory
                getDataFolder().toPath().resolve("cache")
        );

        // ========= CONFIG FACADE =========
        this.configFacade = new BukkitConfigFacade(this);

        // ========= VERSION INFO =========
        this.versionInfo = new VersionInfoAddon(
                getDescription().getVersion(),   // Core version
                "0.0.1",                         // API version (your choice)
                getServer().getName()            // Server brand (Paper, Purpur, etc.)
        );

        // ========= ADDON REGISTRY =========
        this.addonRegistry = new BukkitAddonRegistry(addonsDir, getLogger());

        // ========= API BOOTSTRAP =========
        UtilityX apiImpl = new UtilityX() {
            @Override
            public AddonRegistry addons() {
                return addonRegistry;
            }

            @Override
            public ConfigFacade config() {
                return configFacade;
            }

            @Override
            public Directories directories() {
                return directories;
            }

            @Override
            public VersionInfoAddon version() {
                return versionInfo;
            }
        };

        UtilityXAPI.bootstrap(() -> apiImpl);

        // ========= UI LIB =========
        InvUI.getInstance().setPlugin(this);

        // ========= EVENTS =========
        getServer().getPluginManager().registerEvents(new AddonListener(), this);

        // ========= COMMANDS =========
        getCommand("utilityx").setExecutor(new UtilityXCommand());
        getCommand("addon").setExecutor(new AddonCommand());
        getCommand("UXVersion").setExecutor(new CheckVersionCommand());
        getCommand("UtilityXCore").setExecutor(new UtilityXCoreCommand());

        // ========= LOAD ADDONS =========
        Bukkit.getScheduler().runTask(this, this::loadAddons);
    }


    /**
     * Loads addons (UXAddons/*.jar) on server startup.
     */
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
                                logger.warning("Failed to load addon from: " + file);
                            }
                        } catch (Exception ex) {
                            logger.severe("Error loading addon from: " + file);
                            ex.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            logger.severe("Error scanning addon directory: " + DirectoryHandler.getAddons());
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        logger.info("UtilityXCore has been disabled!");
    }

    // ========= GETTERS =========

    public static UtilityXCore getInstance() {
        return instance;
    }

    public AddonRegistry getAddonRegistry() {
        return addonRegistry;
    }

    public ConfigFacade getConfigFacade() {
        return configFacade;
    }

    public Directories getDirectories() {
        return directories;
    }

    public VersionInfoAddon getVersionInfo() {
        return versionInfo;
    }
}