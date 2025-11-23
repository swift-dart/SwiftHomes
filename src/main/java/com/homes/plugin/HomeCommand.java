package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final HomePlugin plugin;
    private HomeGUI gui;
    
    public HomeCommand(HomePlugin plugin) {
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
        
        if (!player.hasPermission("homeplugin.home")) {
            sender.sendMessage(plugin.getMessageManager().getError("no-permission"));
            return true;
        }
        
        // If args provided and first arg is "help", show help
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender, player);
            return true;
        }
        
        // If no args, open GUI to select home
        if (args.length == 0) {
            if (gui == null) {
                gui = new HomeGUI(plugin);
            }
            gui.openSelectHomeGUI(player);
            return true;
        }
        
        // Teleport to specific home
        String homeName = args[0].toLowerCase();
        Home home = plugin.getHomeManager().getHome(player, homeName);
        
        if (home == null) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "not-found", 
                MessageManager.replacements("name", homeName)));
            return true;
        }
        
        int delay = plugin.getConfig().getInt("teleport-delay", 3);
        if (delay > 0) {
            player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "teleporting", 
                MessageManager.replacements("delay", String.valueOf(delay))));
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(home.getLocation());
                player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "success", 
                    MessageManager.replacements("name", home.getName())));
            }, delay * 20L);
        } else {
            player.teleport(home.getLocation());
            player.sendMessage(plugin.getMessageManager().getCommandMessage("home", "success", 
                MessageManager.replacements("name", home.getName())));
        }
        
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
