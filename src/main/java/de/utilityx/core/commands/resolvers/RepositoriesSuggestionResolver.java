package de.utilityx.core.commands.resolvers;

import de.utilityx.core.UtilityXCore;
import org.bukkit.configuration.file.FileConfiguration;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.command.parameters.CommandParameter;
import studio.mevera.imperat.context.SuggestionContext;
import studio.mevera.imperat.resolvers.SuggestionResolver;


import java.util.List;

public final class RepositoriesSuggestionResolver implements SuggestionResolver<BukkitSource> {

    private final UtilityXCore core;

    public RepositoriesSuggestionResolver(UtilityXCore core) {
        this.core = core;
    }

    @Override
    public List<String> autoComplete(SuggestionContext<BukkitSource> context, CommandParameter<BukkitSource> parameter) {
        FileConfiguration cfg = core.getConfig();
        return cfg.getStringList("repositories");
    }
}