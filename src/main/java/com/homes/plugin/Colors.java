package com.homes.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Colors {
    // Modern color palette
    public static final TextColor PRIMARY = TextColor.fromHexString("#4B5563"); // Dark Gray
    public static final TextColor SECONDARY = TextColor.fromHexString("#A78BFA"); // Purple
    public static final TextColor SUCCESS = TextColor.fromHexString("#34D399"); // Green
    public static final TextColor ERROR = TextColor.fromHexString("#F87171"); // Red
    public static final TextColor WARNING = TextColor.fromHexString("#FBBF24"); // Yellow
    public static final TextColor INFO = TextColor.fromHexString("#4B5563"); // Dark Gray
    public static final TextColor MUTED = TextColor.fromHexString("#9CA3AF"); // Gray
    public static final TextColor ACCENT = TextColor.fromHexString("#FB923C"); // Orange
    
    // Text styles
    public static Component title(String text) {
        return Component.text(text)
            .color(PRIMARY)
            .decoration(TextDecoration.BOLD, true)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component subtitle(String text) {
        return Component.text(text)
            .color(SECONDARY)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component success(String text) {
        return Component.text("✓ " + text)
            .color(SUCCESS)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component error(String text) {
        return Component.text("✗ " + text)
            .color(ERROR)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component info(String text) {
        return Component.text(text)
            .color(INFO)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component muted(String text) {
        return Component.text(text)
            .color(MUTED)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component highlight(String text) {
        return Component.text(text)
            .color(WARNING)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component accent(String text) {
        return Component.text(text)
            .color(ACCENT)
            .decoration(TextDecoration.BOLD, true)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component divider() {
        return Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            .color(MUTED)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    public static Component gradient(String text) {
        return Component.text(text)
            .color(PRIMARY)
            .decoration(TextDecoration.ITALIC, false);
    }
}
