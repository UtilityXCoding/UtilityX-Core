package xyz.blackdev.utilityxcore.impl;

import de.utilityx.api.AddonHandle;
import de.utilityx.api.AddonLifecycleListener;
import de.utilityx.api.AddonRef;
import de.utilityx.api.AddonRegistry;
import de.utilityx.api.AddonState;
import de.utilityx.api.OperationResult;

import xyz.blackdev.utilityxcore.addon.Addon;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class BukkitAddonRegistry implements AddonRegistry {

    private final Path addonsDir;
    private final Logger logger;
    private final List<AddonLifecycleListener> listeners = new ArrayList<>();

    public BukkitAddonRegistry(Path addonsDir, Logger logger) {
        this.addonsDir = addonsDir;
        this.logger = logger;
    }

    // ---------- helpers ----------

    private AddonRef toRef(Addon addon) {
        String id = addon.getName(); // or your id system
        String name = addon.getName();
        String version = addon.getPlugin() != null
                ? addon.getPlugin().getDescription().getVersion()
                : "unknown";

        AddonState state;
        if (addon.getPlugin() == null) {
            state = AddonState.UNLOADED;
        } else if (addon.getPlugin().isEnabled()) {
            state = AddonState.ENABLED;
        } else {
            state = AddonState.DISABLED;
        }

        return new AddonRef(id, name, version, state);
    }

    private Optional<Addon> findRawByIdOrName(String idOrName) {
        return Addon.getAddons().stream()
                .filter(a -> {
                    String name = a.getName();
                    if (name.equalsIgnoreCase(idOrName)) return true;
                    if (a.getPlugin() != null) {
                        return a.getPlugin().getName().equalsIgnoreCase(idOrName);
                    }
                    return false;
                })
                .findFirst();
    }

    private AddonHandle wrap(Addon addon) {
        // Simple wrapper of your Addon into an AddonHandle, if you have a dedicated class use that instead
        return new AddonHandle() {
            @Override public String id()      { return addon.getName(); }
            @Override public String name()    { return addon.getName(); }
            @Override public String version() {
                return addon.getPlugin() != null
                        ? addon.getPlugin().getDescription().getVersion()
                        : "unknown";
            }

            @Override public AddonState state() {
                if (addon.getPlugin() == null) return AddonState.UNLOADED;
                return addon.getPlugin().isEnabled()
                        ? AddonState.ENABLED
                        : AddonState.DISABLED;
            }

            @Override
            public CompletableFuture<OperationResult> enable() {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        addon.load();
                        return OperationResult.ok("Enabled " + id());
                    } catch (Exception e) {
                        return OperationResult.fail("Failed to enable " + id() + ": " + e.getMessage());
                    }
                });
            }

            @Override
            public CompletableFuture<OperationResult> disable() {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        boolean ok = addon.unload();
                        return ok
                                ? OperationResult.ok("Disabled " + id())
                                : OperationResult.fail("Addon " + id() + " was not loaded");
                    } catch (Exception e) {
                        return OperationResult.fail("Failed to disable " + id() + ": " + e.getMessage());
                    }
                });
            }

            @Override
            public CompletableFuture<OperationResult> reload() {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        addon.reload();
                        return OperationResult.ok("Reloaded " + id());
                    } catch (Exception e) {
                        return OperationResult.fail("Failed to reload " + id() + ": " + e.getMessage());
                    }
                });
            }
        };
    }

    // ---------- AddonRegistry API ----------

    @Override
    public List<AddonRef> list() {
        return Addon.getAddons().stream()
                .map(this::toRef)
                .toList();
    }

    @Override
    public Optional<AddonHandle> findById(String id) {
        return findRawByIdOrName(id).map(this::wrap);
    }

    @Override
    public Optional<AddonHandle> findByName(String name) {
        return findRawByIdOrName(name).map(this::wrap);
    }

    @Override
    public CompletableFuture<OperationResult> install(URI uri) {
        // you can implement download logic later
        return CompletableFuture.completedFuture(
                OperationResult.fail("Install from URI not implemented in BukkitAddonRegistry")
        );
    }

    @Override
    public CompletableFuture<OperationResult> install(Path localJar) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!Files.exists(localJar)) {
                    return OperationResult.fail("Source JAR does not exist: " + localJar);
                }
                Path target = addonsDir.resolve(localJar.getFileName());
                Files.copy(localJar, target);
                return OperationResult.ok("Installed addon: " + target.getFileName());
            } catch (Exception e) {
                return OperationResult.fail("Failed to install: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<OperationResult> load(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path jar = addonsDir.resolve(id.endsWith(".jar") ? id : id + ".jar");
                Addon addon = Addon.fetchDataLocally(jar);
                if (addon == null) {
                    return OperationResult.fail("Addon jar not found: " + jar);
                }
                addon.load();
                AddonRef ref = toRef(addon);
                listeners.forEach(l -> l.onLoaded(ref));
                listeners.forEach(l -> l.onEnabled(ref));
                return OperationResult.ok("Loaded addon " + id);
            } catch (Exception e) {
                AddonRef ref = new AddonRef(id, id, "unknown", AddonState.FAILED);
                listeners.forEach(l -> l.onFailed(ref, e.getMessage()));
                return OperationResult.fail("Failed to load addon " + id + ": " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<OperationResult> unload(String id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Addon> opt = findRawByIdOrName(id);
            if (opt.isEmpty()) {
                return OperationResult.fail("Addon not loaded: " + id);
            }
            Addon addon = opt.get();
            boolean ok = addon.unload();
            AddonRef ref = toRef(addon);
            listeners.forEach(l -> l.onDisabled(ref));
            listeners.forEach(l -> l.onUnloaded(ref));
            return ok
                    ? OperationResult.ok("Unloaded addon " + id)
                    : OperationResult.fail("Addon " + id + " was not loaded");
        });
    }

    @Override
    public CompletableFuture<OperationResult> reload(String id) {
        return unload(id).thenCompose(result -> {
            if (!result.success()) {
                return CompletableFuture.completedFuture(result);
            }
            return load(id);
        });
    }

    @Override
    public void addListener(AddonLifecycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(AddonLifecycleListener listener) {
        listeners.remove(listener);
    }
}