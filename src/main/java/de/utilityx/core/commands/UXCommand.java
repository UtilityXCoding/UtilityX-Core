package de.utilityx.core.commands;

import de.utilityx.core.UtilityXCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.Command;
import studio.mevera.imperat.annotations.Usage;

@Command("UX")
public class UXCommand {

    private MiniMessage mm = MiniMessage.miniMessage();
    private Component line = mm.deserialize("<gradient:#e6e1dd:#e6e1dd>==============================</gradient>");
    private Component first = mm.deserialize("<gradient:#e6e1dd:#e6e1dd>I=★=========UtilityX==========★=I</gradient>");
    private Component end = mm.deserialize("<gradient:#e6e1dd:#e6e1dd>I=★========================★=I</gradient>");
    private Component second = mm.deserialize("<gradient:#2a6d73:#2a6d73>Copyright:</gradient>");
    private Component copyright1 = mm.deserialize("<gradient:#2a6d73:#2a6d73>@BlackDev</gradient>");
    private Component copyright2 = mm.deserialize("<gradient:#2a6d73:#2a6d73>@Idiotischer</gradient>");
    private Component copyright3 = mm.deserialize("<gradient:#2a6d73:#2a6d73>@Misieur</gradient>");
    private Component third = mm.deserialize("<gradient:#2a6d73:#2a6d73>Version: " + UtilityXCore.version + "</gradient>");

    private Component finished = first.appendNewline().append(
            second.appendNewline().append(copyright1)
                    .appendNewline().append(copyright2)
            .appendNewline().append(copyright3)
                    .appendNewline().append(line).appendNewline().append(third).appendNewline().append(end)
    );


    @Usage
    public void defaultUsage(BukkitSource source) {
        source.reply(finished);
    }

}
