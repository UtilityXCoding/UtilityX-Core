package de.utilityx.core.managers;

import de.utilityx.core.UtilityXCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AddonStateManager {

    private final File file;
    private FileConfiguration config;

    public AddonStateManager(UtilityXCore core) {
        if (!core.getDataFolder().exists()) {
            core.getDataFolder().mkdirs();
        }
        this.file = new File(core.getDataFolder(), "addons.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized boolean isEnabled(String download) {
        return getEnabledDownloads().contains(download);
    }

    public synchronized List<String> getEnabledDownloads() {
        return new ArrayList<>(config.getStringList("enabled"));
    }

    public synchronized void setEnabled(String download, boolean enabled) {
        List<String> enabledList = new ArrayList<>(config.getStringList("enabled"));
        if (enabled) {
            if (!enabledList.contains(download)) {
                enabledList.add(download);
            }
        } else {
            enabledList.remove(download);
        }
        config.set("enabled", enabledList);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
