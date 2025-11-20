package xyz.blackdev.utilityxcore.impl;

import de.utilityx.api.ConfigFacade;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class BukkitConfigFacade implements ConfigFacade {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public BukkitConfigFacade(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        // ensure config.yml exists
        plugin.saveDefaultConfig();
    }

    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public Set<String> getKeys(String path) {
        if (config.getConfigurationSection(path) == null) {
            return Set.of();
        }
        return config.getConfigurationSection(path).getKeys(false);
    }

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    @Override
    public void save() {
        try {
            plugin.getConfig()
                    .save(plugin.getDataFolder().toPath().resolve("config.yml").toFile());
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }
}