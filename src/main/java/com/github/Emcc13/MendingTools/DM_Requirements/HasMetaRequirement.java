package com.github.Emcc13.MendingTools.DM_Requirements;

import com.github.Emcc13.MendingToolsMain;
import org.bukkit.entity.Player;

public class HasMetaRequirement
        extends Requirement {
    private final String key;
    private final String value;
    private final String type;
    private final boolean invert;

    public HasMetaRequirement(String key, String type, String value, boolean invert) {
        this.key = key;
        this.type = type.toUpperCase();
        this.value = value;
        this.invert = invert;
    }
    public boolean evaluate(MenuHolder holder) {
        Player player = holder.getViewer();
        if (player == null) {
            return false;
        }
        String parsedKey = holder.setPlaceholders(this.key);

        String metaVal = MendingToolsMain.getInstance().getPersistentMetaHandler().getMeta(player, parsedKey, this.type, null);
        if (metaVal == null) {
            return this.invert;
        }

        String expected = holder.setPlaceholders(this.value);
        metaVal = holder.setPlaceholders(metaVal);

        switch (this.type) {
            case "STRING":
            case "BOOLEAN":
                return (this.invert != metaVal.equalsIgnoreCase(expected));
            case "INTEGER":
            case "LONG":
                try {
                    long metaNum = Long.parseLong(metaVal);
                    long toCheck = Long.parseLong(expected);
                    boolean pass = (metaNum >= toCheck);
                    return (this.invert != pass);
                } catch (Exception exception) {}

            case "DOUBLE":
                try {
                    double metaNum = Double.parseDouble(metaVal);
                    double toCheck = Double.parseDouble(expected);
                    boolean pass = (metaNum >= toCheck);
                    return (this.invert != pass);
                } catch (Exception exception) {
                    break;
                }
        }  return this.invert;
    }
}
