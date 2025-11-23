package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomesCommand implements CommandExecutor {
    private final HomePlugin plugin;
    
    public HomesCommand(HomePlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getError("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("homeplugin.homes")) {
            sender.sendMessage(plugin.getMessageManager().getError("no-permission"));
            return true;
        }
        
        // If args provided and first arg is "help", show help
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender, player);
            return true;
        }
        
        // If args provided and first arg is "reload", reload messages
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("homeplugin.admin")) {
                sender.sendMessage(plugin.getMessageManager().getError("no-permission"));
                return true;
            }
            plugin.getMessageManager().reload();
            sender.sendMessage(Component.text("Messages reloaded!").color(NamedTextColor.GREEN));
            return true;
        }
        
        Map<String, Home> homes = plugin.getHomeManager().getPlayerHomes(player);
        
        if (homes.isEmpty()) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("homes", "empty"));
            return true;
        }
        
        player.sendMessage(Component.text(""));
        int max = plugin.getHomeManager().getMaxHomes(player);
        player.sendMessage(plugin.getMessageManager().getCommandMessage("homes", "title", 
            MessageManager.replacements("current", String.valueOf(homes.size()), "max", String.valueOf(max))));
        player.sendMessage(Component.text(""));
        
        for (Home home : homes.values()) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("homes", "list-item", 
                MessageManager.replacements(
                    "name", home.getName(),
                    "world", home.getWorldName(),
                    "x", String.valueOf((int)home.getLocation().getX()),
                    "y", String.valueOf((int)home.getLocation().getY()),
                    "z", String.valueOf((int)home.getLocation().getZ())
                )));
        }
        
        player.sendMessage(Component.text(""));
        
        return true;
    }
    
    private void showHelp(CommandSender sender, Player player) {
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.title"));
        sender.sendMessage(Component.text(""));
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.sethome"));
        if (!player.hasPermission("homeplugin.sethome")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.no-permission"));
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.home"));
        if (!player.hasPermission("homeplugin.home")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.no-permission"));
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.delhome"));
        if (!player.hasPermission("homeplugin.delhome")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.no-permission"));
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.homes"));
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.help-command"));
        
        sender.sendMessage(Component.text(""));
        
        int current = plugin.getHomeManager().getHomeCount(player);
        int max = plugin.getHomeManager().getMaxHomes(player);
        
        if (player.hasPermission("homeplugin.unlimited")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.unlimited-homes", 
                MessageManager.replacements("current", String.valueOf(current))));
        } else {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.help.current-homes", 
                MessageManager.replacements("current", String.valueOf(current), "max", String.valueOf(max))));
        }
    }
}
