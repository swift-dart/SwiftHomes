package com.homes.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {
    private final HomePlugin plugin;
    private final Map<UUID, Map<String, Home>> playerHomes;
    private final File homesFolder;
    
    public HomeManager(HomePlugin plugin) {
        this.plugin = plugin;
        this.playerHomes = new HashMap<>();
        this.homesFolder = new File(plugin.getDataFolder(), "homes");
        
        if (!homesFolder.exists()) {
            homesFolder.mkdirs();
        }
        
        loadAllHomes();
    }
    
    public void setHome(Player player, String homeName, Location location) {
        UUID uuid = player.getUniqueId();
        playerHomes.putIfAbsent(uuid, new HashMap<>());
        
        Map<String, Home> homes = playerHomes.get(uuid);
        homes.put(homeName.toLowerCase(), new Home(homeName, location));
        
        savePlayerHomes(uuid);
    }
    
    public Home getHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = playerHomes.get(uuid);
        
        if (homes == null) {
            return null;
        }
        
        return homes.get(homeName.toLowerCase());
    }
    
    public void deleteHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = playerHomes.get(uuid);
        
        if (homes != null) {
            homes.remove(homeName.toLowerCase());
            savePlayerHomes(uuid);
        }
    }
    
    public Map<String, Home> getPlayerHomes(Player player) {
        UUID uuid = player.getUniqueId();
        return playerHomes.getOrDefault(uuid, new HashMap<>());
    }
    
    public int getHomeCount(Player player) {
        return getPlayerHomes(player).size();
    }
    
    public int getMaxHomes(Player player) {
        // Check for unlimited permission first
        if (player.hasPermission("homeplugin.unlimited")) {
            return Integer.MAX_VALUE;
        }
        
        // Check for tiered permissions (homeplugin.homes.1, homeplugin.homes.3, etc.)
        // Find the highest permission the player has
        int maxFromPermissions = 0;
        for (int i = 1; i <= 100; i++) {
            if (player.hasPermission("homeplugin.homes." + i)) {
                maxFromPermissions = Math.max(maxFromPermissions, i);
            }
        }
        
        // If player has a tiered permission, use that
        if (maxFromPermissions > 0) {
            return maxFromPermissions;
        }
        
        // Otherwise, use config default
        return plugin.getConfig().getInt("max-homes", 5);
    }
    
    public boolean canSetMoreHomes(Player player) {
        return getHomeCount(player) < getMaxHomes(player);
    }
    
    private void loadAllHomes() {
        if (!homesFolder.exists()) {
            return;
        }
        
        for (File file : homesFolder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                String uuidString = file.getName().replace(".yml", "");
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    loadPlayerHomes(uuid);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID file: " + file.getName());
                }
            }
        }
    }
    
    private void loadPlayerHomes(UUID uuid) {
        File file = new File(homesFolder, uuid.toString() + ".yml");
        if (!file.exists()) {
            return;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<String, Home> homes = new HashMap<>();
        
        for (String homeName : config.getKeys(false)) {
            String worldName = config.getString(homeName + ".world");
            double x = config.getDouble(homeName + ".x");
            double y = config.getDouble(homeName + ".y");
            double z = config.getDouble(homeName + ".z");
            float yaw = (float) config.getDouble(homeName + ".yaw");
            float pitch = (float) config.getDouble(homeName + ".pitch");
            
            // Check if world exists, skip if not
            if (plugin.getServer().getWorld(worldName) == null) {
                plugin.getLogger().warning("Skipping home '" + homeName + "' for " + uuid + " - world '" + worldName + "' not found");
                continue;
            }
            
            Location location = new Location(
                plugin.getServer().getWorld(worldName),
                x, y, z, yaw, pitch
            );
            
            homes.put(homeName.toLowerCase(), new Home(homeName, location));
        }
        
        playerHomes.put(uuid, homes);
    }
    
    private void savePlayerHomes(UUID uuid) {
        File file = new File(homesFolder, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        Map<String, Home> homes = playerHomes.get(uuid);
        if (homes == null || homes.isEmpty()) {
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        
        for (Map.Entry<String, Home> entry : homes.entrySet()) {
            String homeName = entry.getKey();
            Home home = entry.getValue();
            Location loc = home.getLocation();
            
            config.set(homeName + ".world", home.getWorldName());
            config.set(homeName + ".x", loc.getX());
            config.set(homeName + ".y", loc.getY());
            config.set(homeName + ".z", loc.getZ());
            config.set(homeName + ".yaw", loc.getYaw());
            config.set(homeName + ".pitch", loc.getPitch());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save homes for " + uuid + ": " + e.getMessage());
        }
    }
    
    public void saveAllHomes() {
        for (UUID uuid : playerHomes.keySet()) {
            savePlayerHomes(uuid);
        }
    }
}
