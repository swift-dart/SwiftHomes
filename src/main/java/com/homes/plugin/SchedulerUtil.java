package com.homes.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Utility class to handle scheduling on both Paper and Folia servers.
 * Folia uses region-based scheduling while Paper uses the traditional scheduler.
 */
public class SchedulerUtil {
    private static boolean isFolia = false;
    private static Method getSchedulerMethod;
    private static Method runDelayedMethod;
    private static Method runMethod;
    
    static {
        try {
            // Try to load Folia's RegionScheduler class
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            isFolia = true;
            
            // Get the methods we need via reflection
            Class<?> serverClass = Bukkit.getServer().getClass();
            getSchedulerMethod = serverClass.getMethod("getRegionScheduler");
            
            Class<?> regionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            runDelayedMethod = regionSchedulerClass.getMethod("runDelayed", Plugin.class, Location.class, java.util.function.Consumer.class, long.class);
            runMethod = regionSchedulerClass.getMethod("run", Plugin.class, Location.class, java.util.function.Consumer.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // Not Folia, use standard Paper/Spigot scheduler
            isFolia = false;
        }
    }
    
    /**
     * Check if the server is running Folia.
     */
    public static boolean isFolia() {
        return isFolia;
    }
    
    /**
     * Run a task at a location after a delay.
     * On Folia, this uses the region scheduler. On Paper, this uses the standard scheduler.
     */
    public static void runDelayed(Plugin plugin, Location location, Runnable task, long delayTicks) {
        if (isFolia) {
            try {
                Object regionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
                runDelayedMethod.invoke(regionScheduler, plugin, location, 
                    (java.util.function.Consumer<Object>) (o) -> task.run(), delayTicks);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to schedule task on Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
    
    /**
     * Run a task at a location immediately.
     * On Folia, this uses the region scheduler. On Paper, this uses the standard scheduler.
     */
    public static void run(Plugin plugin, Location location, Runnable task) {
        if (isFolia) {
            try {
                Object regionScheduler = getSchedulerMethod.invoke(Bukkit.getServer());
                runMethod.invoke(regionScheduler, plugin, location, 
                    (java.util.function.Consumer<Object>) (o) -> task.run());
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to schedule task on Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Run a task for an entity after a delay.
     * On Folia, this uses the entity scheduler. On Paper, this uses the standard scheduler.
     */
    public static void runEntityDelayed(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        if (isFolia) {
            try {
                Class<?> entityClass = entity.getClass();
                Method getSchedulerMethod = entityClass.getMethod("getScheduler");
                Object entityScheduler = getSchedulerMethod.invoke(entity);
                
                Class<?> entitySchedulerClass = entityScheduler.getClass();
                Method runDelayedMethod = entitySchedulerClass.getMethod("runDelayed", Plugin.class, 
                    java.util.function.Consumer.class, Runnable.class, long.class);
                
                runDelayedMethod.invoke(entityScheduler, plugin, 
                    (java.util.function.Consumer<Object>) (o) -> {}, task, delayTicks);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to schedule entity task on Folia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
}
