package xyz.blackdev.utilityxcore.gui.inventory;

import net.kyori.adventure.text.TextComponent;
import xyz.blackdev.utilityxcore.UtilityXCore;
import xyz.blackdev.utilityxcore.gui.items.BackItem;
import xyz.blackdev.utilityxcore.gui.items.ForwardItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SelectionGUI {

    private static final String JSON_URL = "https://raw.githubusercontent.com/UtilityXCoding/UtilityX-Assets/refs/heads/main/ExistingAddons.json";

    public static class AddonItem {
        private String title;
        private String description;
        private String displayitem;
        private boolean enabled = false;

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDisplayitem() { return displayitem; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public static TextComponent titlecomonent;
    }

    public static void openAddonGUI(Player player) {
        CompletableFuture.supplyAsync(() -> fetchAddonData())
                .thenAccept(addonItems -> {
                    org.bukkit.Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        createAndShowGUI(player, addonItems);
                    });
                })
                .exceptionally(throwable -> {
                    player.sendMessage(ChatColor.RED + "Failed to load addon data: " + throwable.getMessage());
                    return null;
                });
    }

    private static List<AddonItem> fetchAddonData() {
        List<AddonItem> items = new ArrayList<>();
        try {
            URL url = new URL(JSON_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");

            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject itemObj = itemsArray.get(i).getAsJsonObject();
                AddonItem item = new AddonItem();
                item.title = itemObj.get("title").getAsString();
                item.description = itemObj.get("description").getAsString();
                item.displayitem = itemObj.get("displayitem").getAsString();
                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private static void createAndShowGUI(Player player, List<AddonItem> addonItems) {
        createAndShowGUI(player, addonItems, 0);
    }

    private static void createAndShowGUI(Player player, List<AddonItem> addonItems, int currentPage) {
        if (addonItems == null || addonItems.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No addon items loaded! Check your JSON file.");
            return;
        }

        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(""));

        int itemsPerPage = 4;
        int totalPages = Math.max(1, (int) Math.ceil((double) addonItems.size() / itemsPerPage));
        final int finalCurrentPage = Math.max(0, Math.min(currentPage, totalPages - 1));

        int startIndex = finalCurrentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, addonItems.size());

        // player.sendMessage(ChatColor.YELLOW + "Debug: Total items: " + addonItems.size() + ", Page: " + (finalCurrentPage + 1) + "/" + totalPages);

        List<AddonItem> pageItems = new ArrayList<>();
        if (startIndex < addonItems.size()) {
            pageItems = addonItems.subList(startIndex, endIndex);
        }

        Item backItem = new SimpleItem(new ItemBuilder(
                finalCurrentPage > 0 ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(finalCurrentPage > 0 ? ChatColor.RED + "Previous Page" : ChatColor.GRAY + "Previous Page (Disabled)")
        ) {
            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
                if (finalCurrentPage > 0) {
                    player.closeInventory();
                    createAndShowGUI(player, addonItems, finalCurrentPage - 1);
                }
            }
        };

        Item forwardItem = new SimpleItem(new ItemBuilder(
                finalCurrentPage < totalPages - 1 ? Material.GREEN_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(finalCurrentPage < totalPages - 1 ? ChatColor.GREEN + "Next Page" : ChatColor.GRAY + "Next Page (Disabled)")
        ) {
            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
                if (finalCurrentPage < totalPages - 1) {
                    player.closeInventory();
                    createAndShowGUI(player, addonItems, finalCurrentPage + 1);
                }
            }
        };

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# # # # # # # # #",
                        "< # # # # # # # >"
                )
                .addIngredient('#', border)
                .addIngredient('.', border)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .build();

        int[] itemSlots = {10, 12, 14, 16};
        for (int i = 0; i < pageItems.size() && i < 4; i++) {
            AddonItem addonItem = pageItems.get(i);

            Material displayMaterial = getMaterialFromString(addonItem.getDisplayitem());

            Item mainItem = new SimpleItem(new ItemBuilder(displayMaterial)
                    .setDisplayName(ChatColor.AQUA + "§l" + addonItem.getTitle())
                    .addLoreLines(ChatColor.DARK_PURPLE + addonItem.getDescription())
            ) {
                @Override
                public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {}
            };

            gui.setItem(itemSlots[i], mainItem);
        }

        int[] toggleSlots = {28, 30, 32, 34};
        for (int i = 0; i < pageItems.size() && i < 4; i++) {
            AddonItem addonItem = pageItems.get(i);

            Material toggleMaterial = addonItem.isEnabled() ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            String toggleStatus = addonItem.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";

            Item toggleItem = new SimpleItem(new ItemBuilder(toggleMaterial)
                    .setDisplayName(ChatColor.YELLOW + "Toggle " + addonItem.getTitle())
                    .addLoreLines(ChatColor.GRAY + "Status: " + toggleStatus)
                    .addLoreLines(ChatColor.GRAY + "Click to toggle")
            ) {
                @Override
                public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
                    addonItem.setEnabled(!addonItem.isEnabled());
                    String status = addonItem.isEnabled() ? "enabled" : "disabled";
                    player.sendMessage(ChatColor.GOLD + addonItem.getTitle() + " has been " + status + "!");
                    player.closeInventory();
                    createAndShowGUI(player, addonItems, finalCurrentPage);
                }
            };

            gui.setItem(toggleSlots[i], toggleItem);
        }

        Window window = Window.single()
                .setViewer(player)
                .setTitle(ChatColor.DARK_AQUA + "UtilityX Addons " + ChatColor.DARK_GRAY + "(Page " + (finalCurrentPage + 1) + "/" + totalPages + ")")
                .setGui(gui)
                .build();

        window.open();
    }

    private static Material getMaterialFromString(String materialName) {
        try {
            if (materialName == null || materialName.isEmpty()) {
                return Material.STONE;
            }

            String processedName = materialName.toLowerCase().trim();

            switch (processedName) {
                case "ender_pearl": return Material.ENDER_PEARL;
                case "red_bed": return Material.RED_BED;
                case "book": return Material.BOOK;
                case "diamond": return Material.DIAMOND;
                case "emerald": return Material.EMERALD;
                case "iron_ingot": return Material.IRON_INGOT;
                case "gold_ingot": return Material.GOLD_INGOT;
                case "chest": return Material.CHEST;
                case "crafting_table": return Material.CRAFTING_TABLE;
                case "furnace": return Material.FURNACE;
                case "compass": return Material.COMPASS;
                case "clock": return Material.CLOCK;
                case "map": return Material.MAP;
                case "paper": return Material.PAPER;
                case "feather": return Material.FEATHER;
                case "arrow": return Material.ARROW;
                case "bow": return Material.BOW;
                case "sword":
                case "diamond_sword": return Material.DIAMOND_SWORD;
                case "pickaxe":
                case "diamond_pickaxe": return Material.DIAMOND_PICKAXE;
                default:
                    String upperName = processedName.toUpperCase();
                    Material material = Material.getMaterial(upperName);
                    if (material != null && material.isItem()) return material;
                    if (upperName.startsWith("MINECRAFT:")) {
                        upperName = upperName.substring(10);
                        material = Material.getMaterial(upperName);
                        if (material != null && material.isItem()) return material;
                    }
                    return Material.STONE;
            }
        } catch (Exception e) {
            System.err.println("Error parsing material: " + materialName + " - " + e.getMessage());
            return Material.STONE;
        }
    }

    private static org.bukkit.plugin.Plugin getPlugin() {
        return UtilityXCore.getPlugin(UtilityXCore.class);
    }
}
