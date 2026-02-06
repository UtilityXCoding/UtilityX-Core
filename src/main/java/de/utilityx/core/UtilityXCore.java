package de.utilityx.core;

import de.utilityx.core.commands.AddonsCommand;
import de.utilityx.core.commands.RepositoryCommand;
import de.utilityx.core.commands.UXCommand;
import de.utilityx.core.commands.resolvers.RepositoriesSuggestionResolver;
import de.utilityx.core.managers.AddonRepositoryManager;
import de.utilityx.core.managers.AddonStateManager;
import de.utilityx.core.managers.AddonManager;
import io.github.mqzen.menus.Lotus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import studio.mevera.imperat.BukkitImperat;

public class UtilityXCore extends JavaPlugin {

  public static String version = "v0.0.1";
  private AddonManager addonManager;
  private AddonRepositoryManager repositoryManager;
  private AddonStateManager addonStateManager;
  public static Lotus lotus;
  private PluginManager pluginManager;
  private Server server;

  @Override
  public void onEnable() {
    BukkitImperat imperat = BukkitImperat.builder(this)
            .applyBrigadier(true)
            .namedSuggestionResolver("repositories", new RepositoriesSuggestionResolver(this))
            .build();
    lotus = Lotus.load(this);
    imperat.registerCommand(new UXCommand());
    imperat.registerCommand(new RepositoryCommand());
    imperat.registerCommand(new AddonsCommand(this));
    saveDefaultConfig();

    addonManager = new AddonManager(this);
    addonStateManager = new AddonStateManager(this);
    repositoryManager = new AddonRepositoryManager(this);
    pluginManager = this.getServer().getPluginManager();
    server = this.getServer();
    Bukkit.getScheduler().runTask(this, addonManager::loadAll);
    repositoryManager.refreshAsync();
    repositoryManager.downloadEnabledAsync();
  }

  @Override
  public void onDisable() {
    if (addonManager != null) addonManager.disableAll();
  }

  public AddonManager getAddonManager() {
    return addonManager;
  }

  public AddonRepositoryManager getRepositoryManager() {
    return repositoryManager;
  }

  public AddonStateManager getAddonStateManager() {
    return addonStateManager;
  }

  public PluginManager getPluginManager() {
    return pluginManager;
  }

  public Server getUXServer() {
    return server;
  }
  public void registerListener(Listener listener) {
    Bukkit.getPluginManager().registerEvents(listener, this);
  }

  public Logger getUXLogger() {
    return this.getSLF4JLogger();
  }

}
