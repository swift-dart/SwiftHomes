package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AnvilGUI implements Listener {
    private final HomePlugin plugin;
    private final HomeGUI homeGUI;
    private final Map<UUID, String> anvilInputs;
    
    public AnvilGUI(HomePlugin plugin, HomeGUI homeGUI) {
        this.plugin = plugin;
        this.homeGUI = homeGUI;
        this.anvilInputs = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openNameInput(Player player) {
        Inventory anvil = Bukkit.createInventory(null, InventoryType.ANVIL, 
            Component.text("Enter Home Name").color(NamedTextColor.DARK_BLUE));
        
        // Add name tag as input
        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = nameTag.getItemMeta();
        meta.displayName(Component.text("home").color(NamedTextColor.WHITE));
        nameTag.setItemMeta(meta);
        anvil.setItem(0, nameTag);
        
        anvilInputs.put(player.getUniqueId(), "");
        player.openInventory(anvil);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        
        if (!anvilInputs.containsKey(uuid)) {
            return;
        }
        
        // Only handle result slot click
        if (event.getRawSlot() == 2) {
            event.setCancelled(true);
            
            ItemStack result = event.getCurrentItem();
            if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                String homeName = extractPlainText(result.getItemMeta().displayName()).toLowerCase();
                
                // Validate home name
                if (homeName.isEmpty()) {
                    player.sendMessage(Component.text("Home name cannot be empty!").color(NamedTextColor.RED));
                    return;
                }
                
                if (homeName.length() > 16) {
                    player.sendMessage(Component.text("Home name must be 16 characters or less!").color(NamedTextColor.RED));
                    return;
                }
                
                if (!homeName.matches("[a-zA-Z0-9_-]+")) {
                    player.sendMessage(Component.text("Home name can only contain letters, numbers, hyphens, and underscores!").color(NamedTextColor.RED));
                    return;
                }
                
                // Check if player can set more homes (unless updating existing)
                Home existing = plugin.getHomeManager().getHome(player, homeName);
                if (existing == null && !plugin.getHomeManager().canSetMoreHomes(player)) {
                    int max = plugin.getHomeManager().getMaxHomes(player);
                    player.sendMessage(Component.text("You have reached the maximum number of homes! (" + max + ")")
                        .color(NamedTextColor.RED));
                    player.closeInventory();
                    return;
                }
                
                // Close and open confirmation GUI
                anvilInputs.remove(uuid);
                player.closeInventory();
                
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    homeGUI.openSetHomeGUI(player, homeName);
                }, 1L);
            }
        } else if (event.getRawSlot() < 2) {
            // Allow clicking first two slots
            event.setCancelled(false);
        } else {
            // Block clicking outside anvil
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            UUID uuid = event.getPlayer().getUniqueId();
            anvilInputs.remove(uuid);
        }
    }
    
    private String extractPlainText(Component component) {
        // Simple text extraction
        String text = component.toString();
        int start = text.indexOf("content=\"");
        if (start == -1) {
            return "";
        }
        start += 9;
        int end = text.indexOf("\"", start);
        if (end == -1) {
            return "";
        }
        return text.substring(start, end);
    }
}
