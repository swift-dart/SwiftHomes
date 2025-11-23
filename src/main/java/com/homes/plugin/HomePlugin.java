package com.homes.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class HomePlugin extends JavaPlugin {
    private static HomePlugin instance;
    private HomeManager homeManager;
    private MessageManager messageManager;
    private HomeGUI homeGUI;
    private ChatInputListener chatInputListener;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        messageManager = new MessageManager(this);
        homeManager = new HomeManager(this);
        homeGUI = new HomeGUI(this);
        chatInputListener = new ChatInputListener(this, homeGUI);
        
        // Register events
        getServer().getPluginManager().registerEvents(chatInputListener, this);
        
        // Register commands
        SetHomeCommand setHomeCmd = new SetHomeCommand(this);
        setHomeCmd.setGUI(homeGUI);
        HomeCommand homeCmd = new HomeCommand(this);
        homeCmd.setGUI(homeGUI);
        DelHomeCommand delHomeCmd = new DelHomeCommand(this);
        delHomeCmd.setGUI(homeGUI);
        
        getCommand("sethome").setExecutor(setHomeCmd);
        getCommand("home").setExecutor(homeCmd);
        getCommand("delhome").setExecutor(delHomeCmd);
        getCommand("homes").setExecutor(new HomesCommand(this));
        
        logger.info("HomePlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (homeManager != null) {
            homeManager.saveAllHomes();
        }
        logger.info("HomePlugin has been disabled!");
    }
    
    public static HomePlugin getInstance() {
        return instance;
    }
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public ChatInputListener getChatInputListener() {
        return chatInputListener;
    }
}
