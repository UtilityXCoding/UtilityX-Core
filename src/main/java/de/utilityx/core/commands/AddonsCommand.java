package de.utilityx.core.commands;

import de.utilityx.core.UtilityXCore;
import de.utilityx.core.gui.AddonGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.annotations.Command;
import studio.mevera.imperat.annotations.Permission;
import studio.mevera.imperat.annotations.SubCommand;
import studio.mevera.imperat.annotations.Usage;

@Command("addons")
@Permission("utilityx.addons")
public class AddonsCommand {

    private final UtilityXCore core;
    private final AddonGUI gui;

    public AddonsCommand(UtilityXCore core) {
        this.core = core;
        this.gui = new AddonGUI(core);
    }

    @Usage
    public void defaultUsage(BukkitSource source) {
        if (source.isConsole()) {
            source.reply(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return;
        }
        UtilityXCore.lotus.openMenu(source.asPlayer(), gui);
    }

    @SubCommand("reload")
    public void reload(BukkitSource source) {
        core.getRepositoryManager().refreshAsync(gui::refreshOpenViews);
        source.reply(Component.text("Reloading addon repositories...", NamedTextColor.YELLOW));
    }

}
