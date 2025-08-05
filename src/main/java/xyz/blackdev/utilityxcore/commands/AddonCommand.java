package xyz.blackdev.utilityxcore.commands;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.blackdev.utilityxcore.addon.Addon;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Inspired by PaperPluginsCommand ~ Idiotischer
 * */
public class AddonCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Collection<Addon> addons = Addon.getAddons();

        if (addons.isEmpty()) {
            sender.sendMessage(Component.text("⚠ No addons are currently loaded.", NamedTextColor.YELLOW));
            return true;
        }

        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("❌ Invalid page!", NamedTextColor.RED));
                return true;
            }
        }

        List<Component> addonC = addons.stream()
                .sorted(Comparator.comparing(Addon::getName))
                .map(addon -> Component.text("- " + addon.getName(), NamedTextColor.GRAY))
                .collect(Collectors.toList());

        List<List<Component>> pages = Lists.partition(addonC, 6);

        if (page < 0 || page >= pages.size()) {
            sender.sendMessage(Component.text("❌ Invalid page " + pages.size(), NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("ℹ Loaded Addons (" + addons.size() + ") - Page " + (page + 1) + "/" + pages.size(), NamedTextColor.GREEN));
        pages.get(page).forEach(sender::sendMessage);

        Component nav = Component.empty();

        if (page > 0) {
            Component prevPage = Component.text("◀ Previous Page", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to view page " + page)))
                    .clickEvent(ClickEvent.runCommand("/" + label + " " + (page - 1)));

            nav = nav.append(prevPage);
        }

        if (page > 0 && page + 1 < pages.size()) {
            nav = nav.append(Component.text(" | ", NamedTextColor.DARK_GRAY));
        }

        if (page + 1 < pages.size()) {
            Component nextPage = Component.text("▶ Next Page", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to view page " + (page + 2))))
                    .clickEvent(ClickEvent.runCommand("/" + label + " " + (page + 1)));

            nav = nav.append(nextPage);
        }

        if (!nav.equals(Component.empty())) {
            sender.sendMessage(nav);
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(Addon.getAddons().isEmpty()) return List.of("⚠ No Addons loaded!");

        return Addon.getAddons().stream().map(Addon::getName).collect(Collectors.toList());
    }
}
