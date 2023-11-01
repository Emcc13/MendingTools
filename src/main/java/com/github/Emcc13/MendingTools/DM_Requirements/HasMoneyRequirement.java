package com.github.Emcc13.MendingTools.DM_Requirements;
import com.github.Emcc13.MendingToolsMain;

import java.util.logging.Level;

public class HasMoneyRequirement
        extends Requirement {
    private final boolean invert;
    private final String placeholder;
    private double amount;

    public HasMoneyRequirement(double amount, boolean invert, String placeholder) {
        this.amount = amount;
        this.invert = invert;
        this.placeholder = placeholder;
    }
    public boolean evaluate(MenuHolder holder) {
        if (getInstance().getVault() == null) {
            return false;
        }

        if (this.placeholder != null) {
            try {
                String expected = holder.setPlaceholders(this.placeholder);
                this.amount = Double.parseDouble(expected);
            } catch (NumberFormatException exception) {
                MendingToolsMain.getInstance().getLogger().log(Level.SEVERE,"Invalid amount found for has money requirement: " + holder
                        .setPlaceholders(this.placeholder), exception);
            }
        }
        if (this.invert) {
            return !getInstance().getVault().hasEnough(holder.getViewer(), this.amount);
        }
        return getInstance().getVault().hasEnough(holder.getViewer(), this.amount);
    }
}
