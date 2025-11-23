package com.homes.plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpHomesCommand implements CommandExecutor {
    private final HomePlugin plugin;
    
    public HelpHomesCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Colors.title("Home Plugin Commands"));
        sender.sendMessage(Component.text(""));
        
        // Commands
        sender.sendMessage(Colors.info("/sethome [name]")
            .append(Colors.muted(" - Set a home at your location")));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("homeplugin.sethome")) {
                sender.sendMessage(Colors.error("  No permission"));
            }
        }
        
        sender.sendMessage(Colors.info("/home [name]")
            .append(Colors.muted(" - Teleport to a home")));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("homeplugin.home")) {
                sender.sendMessage(Colors.error("  No permission"));
            }
        }
        
        sender.sendMessage(Colors.info("/delhome [name]")
            .append(Colors.muted(" - Delete a home")));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("homeplugin.delhome")) {
                sender.sendMessage(Colors.error("  No permission"));
            }
        }
        
        sender.sendMessage(Colors.info("/homes")
            .append(Colors.muted(" - List all your homes")));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("homeplugin.homes")) {
                sender.sendMessage(Colors.error("  No permission"));
            }
        }
        
        sender.sendMessage(Colors.info("/helphomes")
            .append(Colors.muted(" - Show this help menu")));
        
        // Home limits
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int current = plugin.getHomeManager().getHomeCount(player);
            int max = plugin.getHomeManager().getMaxHomes(player);
            
            sender.sendMessage(Component.text(""));
            if (player.hasPermission("homeplugin.unlimited")) {
                sender.sendMessage(Colors.highlight("Your homes: " + current + " (Unlimited)"));
            } else {
                sender.sendMessage(Colors.highlight("Your homes: " + current + "/" + max));
            }
        }
        
        return true;
    }
}
