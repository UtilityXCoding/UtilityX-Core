package xyz.blackdev.utilityxcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.blackdev.utilityxcore.UtilityXCore;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class CheckVersionCommand implements CommandExecutor {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/UtilityXCoding/UtilityX-Assets/refs/heads/main/generalinformation.json";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§3Checking for updates...");
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder json = new StringBuilder();
                while (scanner.hasNextLine()) {
                    json.append(scanner.nextLine());
                }
                String jsonString = json.toString();
                String newestVersion = jsonString.split("\"newestversion\"\\s*:\\s*\"")[1].split("\"")[0];

                if (UtilityXCore.version.equals(newestVersion)) {
                    sender.sendMessage("§aYou are running the latest version (" + UtilityXCore.version + ").");
                } else {
                    sender.sendMessage("§4A new version is available: " + newestVersion + " (You have: " + UtilityXCore.version + ")");
                }
            }
        } catch (Exception e) {
            sender.sendMessage("§cFailed to check for updates: " + e.getMessage());
        }
        return true;
    }
}