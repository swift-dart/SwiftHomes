package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {
    private final HomePlugin plugin;
    private FileConfiguration messages;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    
    public MessageManager(HomePlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        // Create messages.yml if it doesn't exist
        if (!messagesFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try (InputStream in = plugin.getResource("messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create messages.yml: " + e.getMessage());
            }
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public void reload() {
        loadMessages();
    }
    
    public Component getMessage(String path, Map<String, String> replacements) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        
        // Apply replacements
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        return serializer.deserialize(message);
    }
    
    public Component getMessage(String path) {
        return getMessage(path, null);
    }
    
    public List<String> getMessageList(String path) {
        return messages.getStringList(path);
    }
    
    public List<Component> getComponentList(String path, Map<String, String> replacements) {
        List<String> messageList = messages.getStringList(path);
        List<Component> components = new ArrayList<>();
        
        for (String message : messageList) {
            // Apply replacements
            if (replacements != null) {
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    message = message.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
            components.add(serializer.deserialize(message));
        }
        
        return components;
    }
    
    public List<Component> getComponentList(String path) {
        return getComponentList(path, null);
    }
    
    // Convenience methods for common message types
    public Component getCommandMessage(String command, String key, Map<String, String> replacements) {
        return getMessage("commands." + command + "." + key, replacements);
    }
    
    public Component getCommandMessage(String command, String key) {
        return getCommandMessage(command, key, null);
    }
    
    public Component getGuiMessage(String gui, String key, Map<String, String> replacements) {
        return getMessage("gui." + gui + "." + key, replacements);
    }
    
    public Component getGuiMessage(String gui, String key) {
        return getGuiMessage(gui, key, null);
    }
    
    public Component getError(String key, Map<String, String> replacements) {
        return getMessage("errors." + key, replacements);
    }
    
    public Component getError(String key) {
        return getError(key, null);
    }
    
    // Helper to create replacement map
    public static Map<String, String> replacements(String... keyValues) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            if (i + 1 < keyValues.length) {
                map.put(keyValues[i], keyValues[i + 1]);
            }
        }
        return map;
    }
}
