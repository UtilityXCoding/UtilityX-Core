package xyz.blackdev.utilityxcore.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class AddonListener implements Listener {
    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        Addon.getAddons().forEach(addon -> {
            if(addon.plugin.equals(e.getPlugin()) && /* zur sicherheit */ e.getPlugin().getDataPath().equals(e.getPlugin().getDataPath())) {
                addon.unload();
            }
        });
    }
}
