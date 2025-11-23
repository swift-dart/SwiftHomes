package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final HomePlugin plugin;
    private HomeGUI gui;
    
    public DelHomeCommand(HomePlugin plugin) {
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
        
        if (!player.hasPermission("homeplugin.delhome")) {
            sender.sendMessage(plugin.getMessageManager().getError("no-permission"));
            return true;
        }
        
        // If no args, open GUI to select home to delete
        if (args.length == 0) {
            if (gui == null) {
                gui = new HomeGUI(plugin);
            }
            gui.openDeleteHomeGUI(player);
            return true;
        }
        
        // Delete specific home
        String homeName = args[0].toLowerCase();
        Home home = plugin.getHomeManager().getHome(player, homeName);
        
        if (home == null) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("delhome", "not-found", 
                MessageManager.replacements("name", homeName)));
            return true;
        }
        
        plugin.getHomeManager().deleteHome(player, homeName);
        player.sendMessage(plugin.getMessageManager().getCommandMessage("delhome", "success", 
            MessageManager.replacements("name", homeName)));
        
        return true;
    }
}
