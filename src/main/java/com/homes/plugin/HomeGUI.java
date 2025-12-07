package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HomeGUI implements Listener {
    private final HomePlugin plugin;
    private final Map<UUID, GUIType> openGUIs;
    private final Map<UUID, String> pendingHomeName;
    
    public enum GUIType {
        SET_HOME,
        SET_HOME_CONFIRM,
        SELECT_HOME,
        DELETE_HOME
    }
    
    public HomeGUI(HomePlugin plugin) {
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();
        this.pendingHomeName = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openInitialSetHomeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, 
            plugin.getMessageManager().getGuiMessage("set-home", "title", null));
        
        // Fill with black glass pane (modern look)
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }
        
        // Confirm button
        ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(plugin.getMessageManager().getGuiMessage("set-home", "confirm-button", null)
            .decoration(TextDecoration.ITALIC, false));
        confirmMeta.lore(plugin.getMessageManager().getComponentList("gui.set-home.confirm-lore"));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(11, confirm);
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(Component.text("✗ Cancel")
            .color(Colors.ERROR)
            .decoration(TextDecoration.ITALIC, false));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(15, cancel);
        
        // Info item
        ItemStack info = new ItemStack(Material.COMPASS);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(plugin.getMessageManager().getGuiMessage("set-home", "info-title", null)
            .decoration(TextDecoration.ITALIC, false));
        
        Map<String, Home> homes = plugin.getHomeManager().getPlayerHomes(player);
        Map<String, String> replacements = MessageManager.replacements(
            "world", player.getWorld().getName(),
            "x", String.valueOf((int)player.getLocation().getX()),
            "y", String.valueOf((int)player.getLocation().getY()),
            "z", String.valueOf((int)player.getLocation().getZ()),
            "current", String.valueOf(homes.size()),
            "max", String.valueOf(plugin.getHomeManager().getMaxHomes(player))
        );
        infoMeta.lore(plugin.getMessageManager().getComponentList("gui.set-home.info-lore", replacements));
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);
        
        openGUIs.put(player.getUniqueId(), GUIType.SET_HOME);
        player.openInventory(gui);
    }
    
    public void openSetHomeGUI(Player player, String homeName) {
        Map<String, Home> homes = plugin.getHomeManager().getPlayerHomes(player);
        
        Inventory gui = Bukkit.createInventory(null, 27, 
            Component.text("Confirm: " + homeName).color(Colors.PRIMARY));
        
        // Fill with black glass pane
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }
        
        // Confirm button
        ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(Component.text("✓ Confirm Set Home")
            .color(Colors.SUCCESS)
            .decoration(TextDecoration.ITALIC, false));
        confirmMeta.lore(Arrays.asList(
            Component.text("Set home at your current location").color(Colors.MUTED).decoration(TextDecoration.ITALIC, false),
            Component.text("Name: " + homeName).color(Colors.WARNING).decoration(TextDecoration.ITALIC, false)
        ));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(11, confirm);
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(Component.text("✗ Cancel")
            .color(Colors.ERROR)
            .decoration(TextDecoration.ITALIC, false));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(15, cancel);
        
        // Info item
        ItemStack info = new ItemStack(Material.COMPASS);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(Component.text("Home Information")
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("World: " + player.getWorld().getName()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("X: " + (int)player.getLocation().getX()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Y: " + (int)player.getLocation().getY()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Z: " + (int)player.getLocation().getZ()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(""));
        lore.add(Component.text("Homes: " + homes.size() + "/" + plugin.getHomeManager().getMaxHomes(player))
            .color(Colors.WARNING).decoration(TextDecoration.ITALIC, false));
        infoMeta.lore(lore);
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);
        
        openGUIs.put(player.getUniqueId(), GUIType.SET_HOME_CONFIRM);
        pendingHomeName.put(player.getUniqueId(), homeName);
        player.openInventory(gui);
    }
    
    public void openSelectHomeGUI(Player player) {
        Map<String, Home> homes = plugin.getHomeManager().getPlayerHomes(player);
        
        if (homes.isEmpty()) {
            player.sendMessage(Colors.error("You don't have any homes set!"));
            return;
        }
        
        int size = Math.min(54, ((homes.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, 
            Component.text("Select a Home").color(Colors.PRIMARY));
        
        int slot = 0;
        for (Home home : homes.values()) {
            ItemStack item = new ItemStack(Material.RED_BED);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(home.getName())
                .color(Colors.ACCENT)
                .decoration(TextDecoration.ITALIC, false));
            
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World: " + home.getWorldName()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("X: " + (int)home.getLocation().getX()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Y: " + (int)home.getLocation().getY()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Z: " + (int)home.getLocation().getZ()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(""));
            lore.add(Component.text("Click to teleport!").color(Colors.SUCCESS).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            
            item.setItemMeta(meta);
            gui.setItem(slot++, item);
        }
        
        openGUIs.put(player.getUniqueId(), GUIType.SELECT_HOME);
        player.openInventory(gui);
    }
    
    public void openDeleteHomeGUI(Player player) {
        Map<String, Home> homes = plugin.getHomeManager().getPlayerHomes(player);
        
        if (homes.isEmpty()) {
            player.sendMessage(Colors.error("You don't have any homes to delete!"));
            return;
        }
        
        int size = Math.min(54, ((homes.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, 
            Component.text("Delete a Home").color(Colors.ERROR));
        
        int slot = 0;
        for (Home home : homes.values()) {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(home.getName())
                .color(Colors.ERROR)
                .decoration(TextDecoration.ITALIC, false));
            
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World: " + home.getWorldName()).color(Colors.MUTED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(""));
            lore.add(Component.text("Click to delete!").color(Colors.ERROR).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            
            item.setItemMeta(meta);
            gui.setItem(slot++, item);
        }
        
        openGUIs.put(player.getUniqueId(), GUIType.DELETE_HOME);
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        
        if (!openGUIs.containsKey(uuid)) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        
        GUIType guiType = openGUIs.get(uuid);
        
        switch (guiType) {
            case SET_HOME:
                handleInitialSetHomeClick(player, clicked);
                break;
            case SET_HOME_CONFIRM:
                handleSetHomeConfirmClick(player, clicked);
                break;
            case SELECT_HOME:
                handleSelectHomeClick(player, clicked);
                break;
            case DELETE_HOME:
                handleDeleteHomeClick(player, clicked);
                break;
        }
    }
    
    private void handleInitialSetHomeClick(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.LIME_CONCRETE) {
            // Continue - close GUI and ask for name in chat
            player.closeInventory();
            ChatInputListener chatListener = plugin.getChatInputListener();
            if (chatListener != null) {
                chatListener.requestHomeName(player);
            }
        } else if (clicked.getType() == Material.RED_CONCRETE) {
            // Cancel
            player.sendMessage(Colors.muted("Cancelled."));
            player.closeInventory();
        }
    }
    
    private void handleSetHomeConfirmClick(Player player, ItemStack clicked) {
        String homeName = pendingHomeName.get(player.getUniqueId());
        
        if (clicked.getType() == Material.LIME_CONCRETE) {
            // Confirm set home
            if (!plugin.getHomeManager().canSetMoreHomes(player)) {
                Home existing = plugin.getHomeManager().getHome(player, homeName);
                if (existing == null) {
                    player.sendMessage(Colors.error("You have reached the maximum number of homes!"));
                    player.closeInventory();
                    return;
                }
            }
            
            plugin.getHomeManager().setHome(player, homeName, player.getLocation());
            player.sendMessage(Colors.success("Home '" + homeName + "' has been set!"));
            player.closeInventory();
        } else if (clicked.getType() == Material.RED_CONCRETE) {
            // Cancel
            player.sendMessage(Colors.muted("Cancelled."));
            player.closeInventory();
        }
    }
    
    private void handleSelectHomeClick(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.RED_BED && clicked.hasItemMeta()) {
            String homeName = ((ItemMeta)clicked.getItemMeta()).displayName().toString();
            // Extract plain text from Component
            homeName = extractPlainText(clicked.getItemMeta().displayName());
            
            Home home = plugin.getHomeManager().getHome(player, homeName);
            if (home != null) {
                player.closeInventory();
                
                int delay = plugin.getConfig().getInt("teleport-delay", 3);
                if (delay > 0) {
                    player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "teleporting", 
                        MessageManager.replacements("delay", String.valueOf(delay))));
                    
                    SchedulerUtil.runDelayed(plugin, player.getLocation(), () -> {
                        player.teleportAsync(home.getLocation());
                        player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "success", 
                            MessageManager.replacements("name", home.getName())));
                    }, delay * 20L);
                } else {
                    player.teleportAsync(home.getLocation());
                    player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "success", 
                        MessageManager.replacements("name", home.getName())));
                }
            }
        }
    }
    
    private void handleDeleteHomeClick(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.BARRIER && clicked.hasItemMeta()) {
            String homeName = extractPlainText(clicked.getItemMeta().displayName());
            
            plugin.getHomeManager().deleteHome(player, homeName);
            player.sendMessage(Colors.success("Home '" + homeName + "' has been deleted!"));
            player.closeInventory();
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        openGUIs.remove(uuid);
        pendingHomeName.remove(uuid);
    }
    
    private String extractPlainText(Component component) {
        // Simple text extraction - works for basic cases
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
