package com.github.Emcc13.MendingTools.DM_Requirements;
import com.github.Emcc13.MendingTools.Util.ExpUtils;
import com.github.Emcc13.MendingToolsMain;

import java.util.logging.Level;

public class HasExpRequirement
        extends Requirement {
    private final boolean invert;
    private final boolean level;
    private final String amt;

    public HasExpRequirement(String amt, boolean invert, boolean level) {
        this.amt = amt;
        this.invert = invert;
        this.level = level;
    }

    public boolean evaluate(MenuHolder holder) {
        int amount, has = this.level ? holder.getViewer().getLevel() : ExpUtils.getTotalExperience(holder.getViewer());
        try {
            amount = Integer.parseInt(holder.setPlaceholders(this.amt));
        } catch (Exception exception) {
            MendingToolsMain.getInstance().getLogger().log(Level.WARNING,"Invalid amount found for has exp requirement: " + holder
                    .setPlaceholders(this.amt), exception);
            return false;
        }
        if (has < amount) return this.invert;
        return !this.invert;
    }
}