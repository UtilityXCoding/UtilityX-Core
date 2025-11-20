package xyz.blackdev.utilityxcore;

import de.utilityx.api.AddonHandle;
import de.utilityx.api.AddonState;
import de.utilityx.api.OperationResult;
import org.bukkit.plugin.Plugin;
import xyz.blackdev.utilityxcore.addon.Addon;

import java.util.concurrent.CompletableFuture;

public class BukkitAddonHandle implements AddonHandle {

    private final Addon addon;

    public BukkitAddonHandle(Addon addon) {
        this.addon = addon;
    }

    @Override
    public String id() {
        // id == jar name for now
        return addon.getName();
    }

    @Override
    public String name() {
        Plugin p = addon.getPlugin();
        if (p != null) {
            return p.getDescription().getName();
        }
        return addon.getName();
    }

    @Override
    public String version() {
        Plugin p = addon.getPlugin();
        if (p != null) {
            return p.getDescription().getVersion();
        }
        return "unknown";
    }

    @Override
    public AddonState state() {
        Plugin p = addon.getPlugin();
        if (p == null) {
            return AddonState.UNLOADED;
        }
        return p.isEnabled() ? AddonState.ENABLED : AddonState.DISABLED;
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

    public Addon getRawAddon() {
        return addon;
    }
}