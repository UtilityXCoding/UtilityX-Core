package xyz.blackdev.utilityxcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.blackdev.utilityxcore.gui.inventory.SelectionGUI;

public class UtilityXCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        SelectionGUI.openAddonGUI(player);
        return false;
    }
}
