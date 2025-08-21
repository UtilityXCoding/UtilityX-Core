package xyz.blackdev.utilityxcore.handlers;

import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.config.ConfigManager;

import java.nio.file.Paths;

public class ConfigHandler {

    public static ConfigManager configManager = UtilityXCore.configManager;

    public static void createConfigs() {

        //Hier configs erstellen
        configManager.createConfig(Paths.get("enabledaddons.json"));


    }
}
