package de.utilityx.core.commands;

import de.utilityx.core.UtilityXCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.*;
import java.util.ArrayList;
import java.util.List;

@Command("repositories")
@Permission("utilityx.repositorys")
public class RepositoryCommand {

    private final UtilityXCore core = UtilityXCore.getPlugin(UtilityXCore.class);

    @Usage
    public void defaultUsage(BukkitSource source) {
        source.reply(Component.text("Please Choose an Option!").color(NamedTextColor.DARK_RED));
    }

    @SubCommand("list")
    public void list(BukkitSource source) {
        List<String> repos = core.getConfig().getStringList("repositories");
        if (repos.isEmpty()) {
            source.reply(Component.text("No repositories configured").color(NamedTextColor.RED));
            return;
        }
        source.reply(
                Component.text("Repositories:\n", NamedTextColor.GREEN)
                        .append(Component.text("- " + String.join("\n- ", repos), NamedTextColor.GRAY))
        );
    }

    @SubCommand("add")
    public void addRepo(BukkitSource source, @Greedy String repo) {
        List<String> repos = new ArrayList<>(core.getConfig().getStringList("repositories"));

        if (repos.contains(repo)) {
            source.reply(Component.text("Repository already exists").color(NamedTextColor.RED));
            return;
        }

        repos.add(repo);
        core.getConfig().set("repositories", repos);
        core.saveConfig();
        core.getRepositoryManager().refreshAsync();

        source.reply(Component.text("Added repository ").color(NamedTextColor.GREEN)
                .append(Component.text(repo, NamedTextColor.GRAY)));
    }

    @SubCommand("remove")
    public void removeRepo(BukkitSource source, @SuggestionProvider("repositories") @Greedy String repo) {
        List<String> repos = new ArrayList<>(core.getConfig().getStringList("repositories"));

        if (!repos.remove(repo)) {
            source.reply(Component.text("Repository not found").color(NamedTextColor.RED));
            return;
        }

        core.getConfig().set("repositories", repos);
        core.saveConfig();
        core.getRepositoryManager().refreshAsync();

        source.reply(Component.text("Removed repository ").color(NamedTextColor.GREEN)
                .append(Component.text(repo, NamedTextColor.GRAY)));
    }
}
