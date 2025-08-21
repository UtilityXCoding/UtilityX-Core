package xyz.blackdev.utilityxcore.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.util.Optional;

public interface UtilityX {
    AddonRegistry addons();
    ConfigFacade config();
    Directories directories();
    VersionInfo version();

    static Optional<UtilityX> get() {
        RegisteredServiceProvider<UtilityX> reg = Bukkit.getServicesManager().getRegistration(UtilityX.class);
        return Optional.ofNullable(reg == null ? null : reg.getProvider());
    }
}
