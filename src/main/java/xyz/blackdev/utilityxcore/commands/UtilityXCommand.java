package xyz.blackdev.utilityxcore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.blackdev.utilityxcore.gui.inventory.SelectionGUI;

public class UtilityXCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (commandSourceStack.getSender() instanceof Player player) {
            SelectionGUI.openAddonGUI(player);
        } else {
            commandSourceStack.getSender().sendPlainMessage("This command can only be executed by a player.");
        }
    }

    @Override
    public String permission() {
        return "op";
    }
}
