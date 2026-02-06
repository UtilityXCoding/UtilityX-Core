package de.utilityx.core.gui;

import de.utilityx.core.UtilityXCore;
import de.utilityx.core.repository.RepositoryAddon;
import io.github.mqzen.menus.base.Content;
import io.github.mqzen.menus.base.Menu;
import io.github.mqzen.menus.base.MenuView;
import io.github.mqzen.menus.misc.Capacity;
import io.github.mqzen.menus.misc.DataRegistry;
import io.github.mqzen.menus.misc.button.Button;
import io.github.mqzen.menus.misc.button.actions.ButtonClickAction;
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder;
import io.github.mqzen.menus.titles.MenuTitle;
import io.github.mqzen.menus.titles.MenuTitles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AddonGUI implements Menu {
    private final UtilityXCore core;
    private final Map<UUID, Integer> pages = new ConcurrentHashMap<>();
    private final Map<UUID, MenuView<?>> openViews = new ConcurrentHashMap<>();

    public AddonGUI(UtilityXCore core) {
        this.core = core;
    }

    @Override
    public String getName() {
        return "utilityx_addons";
    }

    @Override
    public InventoryType getMenuType() {
        return Menu.super.getMenuType();
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry dataRegistry, Player player) {
        return MenuTitles.createModern(Component.text("UtilityX | Addons").color(TextColor.fromHexString("#2a6d73")));
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry dataRegistry, Player player) {
        return Capacity.of(4,9);
    }

    @Override
    public @NotNull Content getContent(DataRegistry dataRegistry, Player player, Capacity capacity) {
        Content.Builder builder = Content.builder(capacity);
        fillBorder(builder, capacity);

        List<RepositoryAddon> addons = core.getRepositoryManager().getCachedAddons();
        int pageSize = 7;
        int page = pages.getOrDefault(player.getUniqueId(), 0);
        int maxPage = addons.isEmpty() ? 0 : (addons.size() - 1) / pageSize;
        if (page > maxPage) {
            page = maxPage;
            pages.put(player.getUniqueId(), page);
        }

        if (addons.isEmpty()) {
            builder.setButton(22, Button.empty(
                    ItemBuilder.modern(Material.BARRIER)
                            .setDisplay(Component.text("No addons found", NamedTextColor.RED))
                            .setLore(List.of(Component.text("Check your repository URLs.", NamedTextColor.GRAY)))
                            .build()
            ));
            return builder.build();
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, addons.size());
        int col = 1;
        for (int i = start; i < end; i++) {
            RepositoryAddon addon = addons.get(i);
            boolean enabled = core.getAddonStateManager().isEnabled(addon.getDownload());
            builder.setButton(1, col, createAddonButton(addon, enabled));
            builder.setButton(2, col, createStatusPane(enabled));
            col++;
        }

        builder.setButton(3, 0, createNavButton("Previous", NamedTextColor.GRAY, page > 0, -1));
        builder.setButton(3, 8, createNavButton("Next", NamedTextColor.GRAY, page < maxPage, 1));
        builder.setButton(3, 4, Button.empty(
                ItemBuilder.modern(Material.PAPER)
                        .setDisplay(Component.text((page + 1) + " / " + (maxPage + 1), NamedTextColor.WHITE))
                        .build()
        ));

        return builder.build();
    }

    @Override
    public boolean onPreClick(MenuView<?> playerMenuView, InventoryClickEvent event) {
        if (event.getClickedInventory() == playerMenuView.getInventory()) {
            event.setCancelled(true);
            return true;
        }
        return Menu.super.onPreClick(playerMenuView, event);
    }

    @Override
    public void onPostClick(MenuView<?> playerMenuView, InventoryClickEvent event) {
        Menu.super.onPostClick(playerMenuView, event);
    }

    @Override
    public void onClose(MenuView<?> playerMenuView, InventoryCloseEvent event) {
        playerMenuView.getPlayer().ifPresent(player -> openViews.remove(player.getUniqueId()));
        Menu.super.onClose(playerMenuView, event);
    }

    @Override
    public void onOpen(MenuView<?> playerMenuView, InventoryOpenEvent event) {
        playerMenuView.getPlayer().ifPresent(player -> pages.putIfAbsent(player.getUniqueId(), 0));
        playerMenuView.getPlayer().ifPresent(player -> openViews.put(player.getUniqueId(), playerMenuView));
        Menu.super.onOpen(playerMenuView, event);
    }

    @Override
    public void onDrag(MenuView<?> playerMenuView, InventoryDragEvent event) {
        if (event.getInventory() == playerMenuView.getInventory()) {
            event.setCancelled(true);
        }
        Menu.super.onDrag(playerMenuView, event);
    }

    private Button createAddonButton(RepositoryAddon addon, boolean enabled) {
        Material material = parseMaterial(addon.getItem());
        Component name = Component.text(safe(addon.getName()), NamedTextColor.WHITE);
        Component description = Component.text(safe(addon.getDescription()), NamedTextColor.LIGHT_PURPLE);
        Component author = Component.text("Author: " + safe(addon.getAuthor()), NamedTextColor.LIGHT_PURPLE);
        Component status = Component.text(enabled ? "Enabled" : "Disabled",
                enabled ? NamedTextColor.GREEN : NamedTextColor.RED);
        Component hint = Component.text(enabled ? "Click to disable" : "Click to enable", NamedTextColor.GRAY);

        return Button.clickable(
                ItemBuilder.modern(material)
                        .setDisplay(name)
                        .setLore(List.of(description, author, status, hint))
                        .build(),
                ButtonClickAction.plain((view, click) -> {
                    core.getRepositoryManager().toggleAddon(addon, view.getPlayer().orElse(null));
                    boolean nowEnabled = core.getAddonStateManager().isEnabled(addon.getDownload());
                    view.replaceClickedButton(click, createAddonButton(addon, nowEnabled));
                    view.replaceButton(click.getSlot() + 9, createStatusPane(nowEnabled));
                })
        );
    }

    private Button createStatusPane(boolean enabled) {
        Material material = enabled ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        Component title = Component.text(enabled ? "Enabled" : "Disabled",
                enabled ? NamedTextColor.GREEN : NamedTextColor.RED);
        return Button.empty(ItemBuilder.modern(material).setDisplay(title).build());
    }

    private Button createNavButton(String label, NamedTextColor color, boolean active, int delta) {
        Material material = active ? Material.ARROW : Material.GRAY_STAINED_GLASS_PANE;
        return Button.clickable(
                ItemBuilder.modern(material)
                        .setDisplay(Component.text(label, active ? color : NamedTextColor.DARK_GRAY))
                        .build(),
                ButtonClickAction.plain((view, click) -> {
                    if (!active) return;
                    view.getPlayer().ifPresent(player -> {
                        pages.compute(player.getUniqueId(), (k, v) -> (v == null ? 0 : v) + delta);
                        view.refresh();
                    });
                })
        );
    }

    private void fillBorder(Content.Builder builder, Capacity capacity) {
        int rows = capacity.getRows();
        int cols = capacity.getColumns();
        Button border = Button.empty(
                ItemBuilder.modern(Material.GRAY_STAINED_GLASS_PANE)
                        .setDisplay(Component.text(" ", NamedTextColor.DARK_GRAY))
                        .build()
        );
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean isBorder = r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
                if (isBorder) {
                    builder.setButton(r, c, border);
                }
            }
        }
    }

    private Material parseMaterial(String item) {
        if (item == null || item.isBlank()) return Material.BARRIER;
        Material material = Material.matchMaterial(item.trim());
        return material == null ? Material.BARRIER : material;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public void refreshOpenViews() {
        for (MenuView<?> view : openViews.values()) {
            view.refresh();
        }
    }
}
