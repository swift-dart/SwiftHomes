package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final HomePlugin plugin;
    private HomeGUI gui;
    
    public SetHomeCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }
    
    public void setGUI(HomeGUI gui) {
        this.gui = gui;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getError("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("swifthomes.sethome")) {
            sender.sendMessage(plugin.getMessageManager().getError("no-permission"));
            return true;
        }
        
        // If args provided, use that name directly
        if (args.length > 0) {
            String homeName = args[0].toLowerCase();
            
            // Check if player can set more homes (unless updating existing)
            Home existing = plugin.getHomeManager().getHome(player, homeName);
            if (existing == null && !plugin.getHomeManager().canSetMoreHomes(player)) {
                int current = plugin.getHomeManager().getHomeCount(player);
                int max = plugin.getHomeManager().getMaxHomes(player);
                player.sendMessage(plugin.getMessageManager().getCommandMessage("sethome", "limit-reached", 
                    MessageManager.replacements("current", String.valueOf(current), "max", String.valueOf(max))));
                return true;
            }
            
            // Open confirmation GUI
            if (gui == null) {
                gui = new HomeGUI(plugin);
            }
            gui.openSetHomeGUI(player, homeName);
        } else {
            // Open initial GUI (green/red blocks)
            if (gui == null) {
                gui = new HomeGUI(plugin);
            }
            gui.openInitialSetHomeGUI(player);
        }
        
        return true;
    }
}
