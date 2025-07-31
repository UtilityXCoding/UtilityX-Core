package xyz.blackdev.utilityxcore.handlers;

import jdk.jshell.execution.Util;
import org.bukkit.plugin.Plugin;
import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.utils.DirectoryUtil;

import static org.bukkit.Bukkit.getServer;

public class DirectoryHandler {
    public static void CreateDirectories() {
        Plugin plugin = UtilityXCore.getPlugin(UtilityXCore.class);

        DirectoryUtil.CreateUXSubDirectory(UtilityXCore.logger, getServer().getWorldContainer().getAbsolutePath(), "UXAddons");
        DirectoryUtil.CreateUXSubDirectory(UtilityXCore.logger, getServer().getWorldContainer().getAbsolutePath(), "UXConfigs");
        DirectoryUtil.CreateUXSubDirectory(UtilityXCore.logger, getServer().getWorldContainer().getAbsolutePath(), "UXPlayerData");

    }

}
