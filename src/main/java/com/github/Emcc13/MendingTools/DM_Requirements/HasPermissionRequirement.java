package com.github.Emcc13.MendingTools.DM_Requirements;

public class HasPermissionRequirement
        extends Requirement {
    private final String perm;
    private final boolean invert;

    public HasPermissionRequirement(String permission, boolean invert) {
        this.perm = permission;
        this.invert = invert;
    }
    public boolean evaluate(MenuHolder holder) {
        String check = holder.setPlaceholders(this.perm);
        if (this.invert) {
            return !holder.getViewer().hasPermission(check);
        }
        return holder.getViewer().hasPermission(check);
    }
}
