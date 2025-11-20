package xyz.blackdev.utilityxcore.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import xyz.blackdev.utilityxcore.addon.Addon;

public class AddonListener implements Listener {

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        Addon.getAddons().forEach(addon -> {
            if (addon.getPlugin() != null && addon.getPlugin().equals(e.getPlugin())) {
                addon.unload();
            }
        });
    }
}