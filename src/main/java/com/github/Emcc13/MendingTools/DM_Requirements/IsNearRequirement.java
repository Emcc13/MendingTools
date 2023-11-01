package com.github.Emcc13.MendingTools.DM_Requirements;

import org.bukkit.Location;

public class IsNearRequirement
        extends Requirement {
    private final Location location;
    private final int distance;
    private final boolean invert;

    public IsNearRequirement(Location location, int distance, boolean invert) {
        this.location = location;
        this.distance = distance;
        this.invert = invert;
    }
    public boolean evaluate(MenuHolder holder) {
        if (holder.getViewer() == null) {
            return false;
        }
        if (this.location.getWorld() == null){
            this.location.setWorld(holder.getViewer().getLocation().getWorld());
        }
        boolean withinRange = (holder.getViewer().getLocation().distance(this.location) < this.distance);
        if (!holder.getViewer().getWorld().getName().equals(this.location.getWorld().getName())) {
            withinRange = false;
        }
        return this.invert == (!withinRange);
    }
}
