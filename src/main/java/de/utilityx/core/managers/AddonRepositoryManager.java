package de.utilityx.core.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.utilityx.core.UtilityXCore;
import de.utilityx.core.repository.RepositoryAddon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AddonRepositoryManager {

    private final UtilityXCore core;
    private final Gson gson = new Gson();
    private volatile List<RepositoryAddon> cachedAddons = Collections.emptyList();

    public AddonRepositoryManager(UtilityXCore core) {
        this.core = core;
    }

    public List<RepositoryAddon> getCachedAddons() {
        return new ArrayList<>(cachedAddons);
    }

    public void refreshAsync() {
        refreshAsync(null);
    }

    public void refreshAsync(Runnable onComplete) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            List<String> repos = core.getConfig().getStringList("repositories");
            Map<String, RepositoryAddon> collected = new LinkedHashMap<>();
            for (String repoUrl : repos) {
                if (repoUrl == null || repoUrl.isBlank()) continue;
                try {
                    String payload = readUrl(repoUrl);
                    List<RepositoryAddon> parsed = parseRepositoryPayload(payload);
                    for (RepositoryAddon addon : parsed) {
                        if (addon == null || addon.getDownload() == null) continue;
                        collected.putIfAbsent(addon.getDownload(), addon);
                    }
                } catch (Exception e) {
                    core.getLogger().warning("Failed to load repository: " + repoUrl + " (" + e.getMessage() + ")");
                }
            }
            cachedAddons = new ArrayList<>(collected.values());
            if (onComplete != null) {
                Bukkit.getScheduler().runTask(core, onComplete);
            }
        });
    }

    public void downloadEnabledAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            List<String> enabledDownloads = core.getAddonStateManager().getEnabledDownloads();
            for (String download : enabledDownloads) {
                if (download == null || download.isBlank()) continue;
                File target = getJarFile(download, null);
                if (target.exists()) continue;
                downloadToFile(download, target);
                Bukkit.getScheduler().runTask(core, () -> {
                    if (core.getAddonStateManager().isEnabled(download) && target.exists()) {
                        core.getAddonManager().loadOne(target);
                    }
                });
            }
        });
    }

    public void toggleAddon(RepositoryAddon addon, Player player) {
        String download = safe(addon.getDownload());
        if (download.isEmpty()) {
            send(player, "Addon has no download link.", NamedTextColor.RED);
            return;
        }

        if (!download.toLowerCase().endsWith(".jar")) {
            send(player, "Download link must end with .jar", NamedTextColor.RED);
            return;
        }

        if (core.getAddonStateManager().isEnabled(download)) {
            disableAddon(download, player);
        } else {
            enableAddon(addon, player);
        }
    }

    private void enableAddon(RepositoryAddon addon, Player player) {
        String download = safe(addon.getDownload());
        core.getAddonStateManager().setEnabled(download, true);

        File target = getJarFile(download, addon.getName());
        send(player, "Enabling addon: " + safe(addon.getName()), NamedTextColor.YELLOW);

        if (target.exists()) {
            Bukkit.getScheduler().runTask(core, () -> core.getAddonManager().loadOne(target));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            boolean ok = downloadToFile(download, target);
            Bukkit.getScheduler().runTask(core, () -> {
                if (!core.getAddonStateManager().isEnabled(download)) {
                    return;
                }
                if (ok && target.exists()) {
                    core.getAddonManager().loadOne(target);
                    send(player, "Downloaded addon: " + safe(addon.getName()), NamedTextColor.GREEN);
                } else if (player != null) {
                    send(player, "Failed to download addon: " + safe(addon.getName()), NamedTextColor.RED);
                }
            });
        });
    }

    private void disableAddon(String download, Player player) {
        core.getAddonStateManager().setEnabled(download, false);
        File target = getJarFile(download, null);
        if (core.getAddonManager().isLoaded(target)) {
            core.getAddonManager().disableJar(target);
        }
        if (target.exists() && !target.delete()) {
            core.getLogger().warning("Failed to delete addon jar: " + target.getAbsolutePath());
        }
        send(player, "Disabled addon.", NamedTextColor.RED);
    }

    private String readUrl(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("User-Agent", "UtilityX-Core");
        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private boolean downloadToFile(String url, File target) {
        try {
            if (!target.getParentFile().exists() && !target.getParentFile().mkdirs()) {
                return false;
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("User-Agent", "UtilityX-Core");
            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(target)) {
                in.transferTo(out);
            }
            return true;
        } catch (Exception e) {
            core.getLogger().warning("Failed to download addon: " + url + " (" + e.getMessage() + ")");
            try {
                Files.deleteIfExists(target.toPath());
            } catch (Exception ignored) {
            }
            return false;
        }
    }

    private List<RepositoryAddon> parseRepositoryPayload(String payload) {
        String trimmed = payload == null ? "" : payload.trim();
        if (trimmed.isEmpty()) return Collections.emptyList();

        try {
            JsonElement element = JsonParser.parseString(trimmed);
            return parseElement(element);
        } catch (Exception ignored) {
        }

        if (!trimmed.startsWith("[") && trimmed.contains("},")) {
            try {
                JsonElement element = JsonParser.parseString("[" + trimmed + "]");
                return parseElement(element);
            } catch (Exception ignored) {
            }
        }

        return Collections.emptyList();
    }

    private List<RepositoryAddon> parseElement(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return Collections.emptyList();
        }
        if (element.isJsonArray()) {
            Type listType = new TypeToken<List<RepositoryAddon>>() {}.getType();
            return gson.fromJson(element, listType);
        }
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("addons") && obj.get("addons").isJsonArray()) {
                JsonArray array = obj.getAsJsonArray("addons");
                Type listType = new TypeToken<List<RepositoryAddon>>() {}.getType();
                return gson.fromJson(array, listType);
            }
            RepositoryAddon single = gson.fromJson(obj, RepositoryAddon.class);
            return single == null ? Collections.emptyList() : List.of(single);
        }
        return Collections.emptyList();
    }

    private File getJarFile(String download, String fallbackName) {
        String fileName = fallbackName;
        try {
            URL url = new URL(download);
            String path = url.getPath();
            int idx = path.lastIndexOf('/');
            if (idx >= 0 && idx + 1 < path.length()) {
                fileName = path.substring(idx + 1);
            }
        } catch (Exception ignored) {
        }
        if (fileName == null || fileName.isBlank()) {
            fileName = "addon.jar";
        }
        if (!fileName.toLowerCase().endsWith(".jar")) {
            fileName = fileName + ".jar";
        }
        File dir = new File(core.getDataFolder(), "addons");
        return new File(dir, fileName);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void send(Player player, String message, NamedTextColor color) {
        if (player == null) return;
        player.sendMessage(Component.text(message, color));
    }
}
