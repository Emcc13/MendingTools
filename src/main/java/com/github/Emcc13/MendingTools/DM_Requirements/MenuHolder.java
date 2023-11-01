package com.github.Emcc13.MendingTools.DM_Requirements;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class MenuHolder {
    private final Player viewer;
    private Player placeholderPlayer;
    private String menuName;
    private BukkitTask updateTask = null;
    private Inventory inventory;
    private boolean updating;
    private Map<String, String> typedArgs;

    public MenuHolder(Player viewer) {
        this.viewer = viewer;
    }
    public MenuHolder(Player viewer, String menuName, Inventory inventory) {
        this.viewer = viewer;
        this.menuName = menuName;
        this.inventory = inventory;
    }

    public String getViewerName() {
        return this.viewer.getName();
    }

    public BukkitTask getUpdateTask() {
        return this.updateTask;
    }

    public Player getViewer() {
        return this.viewer;
    }

    public String setPlaceholders(String string) {
        if (this.typedArgs == null || this.typedArgs.isEmpty()) {
            if (this.placeholderPlayer != null) return PlaceholderAPI.setPlaceholders((OfflinePlayer)this.placeholderPlayer, string);
            return (getViewer() == null) ? string :
                    PlaceholderAPI.setPlaceholders((OfflinePlayer)getViewer(), string);
        }
        for (Map.Entry<String, String> entry : this.typedArgs.entrySet()) {
            string = string.replace("{" + (String)entry.getKey() + "}", entry.getValue());
        }
        if (this.placeholderPlayer != null) return PlaceholderAPI.setPlaceholders((OfflinePlayer)this.placeholderPlayer, string);
        return (getViewer() == null) ? string :
                PlaceholderAPI.setPlaceholders((OfflinePlayer)getViewer(), string);
    }

    public String setArguments(String string) {
        if (this.typedArgs == null || this.typedArgs.isEmpty()) {
            return string;
        }
        for (Map.Entry<String, String> entry : this.typedArgs.entrySet()) {
            string = string.replace("{" + (String)entry.getKey() + "}", entry.getValue());
        }
        return string;
    }
}
