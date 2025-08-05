package xyz.blackdev.utilityxcore.handlers;

import org.bukkit.plugin.Plugin;
import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.utils.DirectoryUtil;

import java.nio.file.Path;

import static org.bukkit.Bukkit.getServer;

public class DirectoryHandler {
    private static Path addons;
    public static void createDirectories() {
        addons = DirectoryUtil.createUXSubDirectory(UtilityXCore.logger, UtilityXCore.getInstance().getDataPath().toString(), "UXAddons");
        DirectoryUtil.createUXSubDirectory(UtilityXCore.logger, UtilityXCore.getInstance().getDataPath().toString(), "UXConfigs");
        DirectoryUtil.createUXSubDirectory(UtilityXCore.logger, UtilityXCore.getInstance().getDataPath().toString(), "UXPlayerData");
    }

    public static Path getAddons() {
        return addons;
    }
}
