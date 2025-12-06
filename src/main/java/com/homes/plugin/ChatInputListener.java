package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputListener implements Listener {
    private final HomePlugin plugin;
    private final HomeGUI homeGUI;
    private final Map<UUID, PendingInput> pendingInputs;
    
    public enum InputType {
        HOME_NAME
    }
    
    private static class PendingInput {
        InputType type;
        
        PendingInput(InputType type) {
            this.type = type;
        }
    }
    
    public ChatInputListener(HomePlugin plugin, HomeGUI homeGUI) {
        this.plugin = plugin;
        this.homeGUI = homeGUI;
        this.pendingInputs = new HashMap<>();
    }
    
    public void requestHomeName(Player player) {
        pendingInputs.put(player.getUniqueId(), new PendingInput(InputType.HOME_NAME));
        player.sendMessage(Component.text(""));
        player.sendMessage(Colors.divider());
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  ").append(plugin.getMessageManager().getCommandMessage("sethome", "enter-name-title")));
        player.sendMessage(Component.text("  ").append(plugin.getMessageManager().getCommandMessage("sethome", "enter-name-cancel")));
        player.sendMessage(Component.text(""));
        player.sendMessage(Colors.divider());
        player.sendMessage(Component.text(""));
    }
    
    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!pendingInputs.containsKey(uuid)) {
            return;
        }
        
        event.setCancelled(true);
        
        PendingInput input = pendingInputs.remove(uuid);
        String message = event.getMessage().trim();
        
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("sethome", "cancelled"));
            return;
        }
        
        if (input.type == InputType.HOME_NAME) {
            handleHomeNameInput(player, message);
        }
    }
    
    private void handleHomeNameInput(Player player, String homeName) {
        // Validate home name
        if (homeName.isEmpty()) {
            player.sendMessage(plugin.getMessageManager().getError("sethome.empty-name"));
            requestHomeName(player);
            return;
        }
        
        if (homeName.length() > 16) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("sethome", "name-too-long"));
            requestHomeName(player);
            return;
        }
        
        if (!homeName.matches("[a-zA-Z0-9_-]+")) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("sethome", "invalid-name"));
            requestHomeName(player);
            return;
        }
        
        // Check if player can set more homes (unless updating existing)
        Home existing = plugin.getHomeManager().getHome(player, homeName);
        if (existing == null && !plugin.getHomeManager().canSetMoreHomes(player)) {
            int current = plugin.getHomeManager().getHomeCount(player);
            int max = plugin.getHomeManager().getMaxHomes(player);
            player.sendMessage(plugin.getMessageManager().getCommandMessage("sethome", "limit-reached", 
                MessageManager.replacements("current", String.valueOf(current), "max", String.valueOf(max))));
            return;
        }
        
        // Open GUI to confirm
        SchedulerUtil.run(plugin, player.getLocation(), () -> {
            homeGUI.openSetHomeGUI(player, homeName);
        });
    }
}
