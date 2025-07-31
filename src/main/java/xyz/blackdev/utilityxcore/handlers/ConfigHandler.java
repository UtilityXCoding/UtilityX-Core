package xyz.blackdev.utilityxcore.handlers;

import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.config.ConfigUtil;

public class ConfigHandler {

    public static void CreateConfigs() {

        ConfigUtil CoreXConfig = UtilityXCore.manager.getConfig("CoreXConfig");
        CoreXConfig.get("home-addon", "true");
        CoreXConfig.get("tpa-addon", "true");
        CoreXConfig.get("msg-addon", "true");

        UtilityXCore.manager.saveAll();
    }
}
