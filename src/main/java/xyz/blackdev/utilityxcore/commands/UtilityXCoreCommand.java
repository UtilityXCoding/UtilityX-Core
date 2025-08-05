package xyz.blackdev.utilityxcore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UtilityXCoreCommand implements CommandExecutor {

    public static TextComponent message;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        message = Component.text("Hey " + player.getName() + " you ran the UtilityXCore command!")
                .color(NamedTextColor.DARK_AQUA)
                .decoration(TextDecoration.BOLD, false)
                .append(Component.text()
                        .content("\n UtilityX is a plugin that is supposed to replace EssentialsX, and provide a better experience for players and Server Owners.")
                        .decoration(TextDecoration.BOLD, false)
                        )
        ;
        player.sendMessage(message);
        return false;
    }
}
