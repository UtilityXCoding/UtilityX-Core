package xyz.blackdev.utilityxcore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class UtilityXCoreCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        commandSourceStack.getSender().sendMessage(Component.text("Hey " + commandSourceStack.getSender().getName() + " you ran the UtilityXCore command!")
                .color(NamedTextColor.DARK_AQUA)
                .decoration(TextDecoration.BOLD, false)
                .append(Component.text()
                        .content("\n UtilityX is a plugin that is supposed to replace EssentialsX, and provide a better experience for players and Server Owners.")
                        .decoration(TextDecoration.BOLD, false)
                )
        );
    }

    @Override
    public String permission() {
        return "op";
    }
}
