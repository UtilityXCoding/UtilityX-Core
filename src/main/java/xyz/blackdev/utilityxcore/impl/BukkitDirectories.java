package xyz.blackdev.utilityxcore.impl;

import de.utilityx.api.Directories;

import java.nio.file.Path;

/**
 * Bukkit-specific implementation of the UtilityX Directories API.
 */
public class BukkitDirectories implements Directories {

    private final Path root;
    private final Path data;
    private final Path addons;
    private final Path cache;

    public BukkitDirectories(Path root, Path data, Path addons, Path cache) {
        this.root = root;
        this.data = data;
        this.addons = addons;
        this.cache = cache;
    }

    @Override
    public Path root() {
        return root;
    }

    @Override
    public Path data() {
        return data;
    }

    @Override
    public Path addons() {
        return addons;
    }

    @Override
    public Path cache() {
        return cache;
    }
}