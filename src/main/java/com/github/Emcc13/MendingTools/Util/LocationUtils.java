package com.github.Emcc13.MendingTools.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class LocationUtils
{
    @NotNull
    public static String serializeLocation(@NotNull Location loc) {
        World world = loc.getWorld();
        if (world == null) {
            return "" + loc.getX() + "," + loc.getX() + "," + loc
                    .getY();
        }

        return loc.getWorld().getName() + "," + loc.getWorld().getName() + "," + loc
                .getX() + "," + loc
                .getY();
    }

    @Nullable
    public static Location deserializeLocation(@NotNull String loc) throws NumberFormatException {
        if (!loc.contains(",")) {
            return null;
        }

        String[] data = loc.split(",", 4);

        if (data.length < 3 || data.length > 4) {
            return null;
        }

        if (data.length == 3) {
            return new Location(null,

                    Double.parseDouble(data[0]),
                    Double.parseDouble(data[1]),
                    Double.parseDouble(data[2]));
        }

        return new Location(
                Bukkit.getServer().getWorld(data[0]),
                Double.parseDouble(data[1]),
                Double.parseDouble(data[2]),
                Double.parseDouble(data[3]));
    }
}
