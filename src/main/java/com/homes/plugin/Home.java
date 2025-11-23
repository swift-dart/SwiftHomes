package com.homes.plugin;

import org.bukkit.Location;
import java.io.Serializable;

public class Home implements Serializable {
    private final String name;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    
    public Home(String name, Location location) {
        this.name = name;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return new Location(
            HomePlugin.getInstance().getServer().getWorld(worldName),
            x, y, z, yaw, pitch
        );
    }
    
    public String getWorldName() {
        return worldName;
    }
}
