package com.github.Emcc13.MendingTools.Util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class ExpUtils {
    private ExpUtils() {
        throw new AssertionError("Util classes should not be initialized");
    }
    public static void setExp(@NotNull Player target, @NotNull String stringAmount) throws NumberFormatException {
        long amount;
        String lowerCase = stringAmount.toLowerCase(Locale.ENGLISH);

        if (stringAmount.contains("l")) {
            int neededLevel = Integer.parseInt(lowerCase.replaceAll("l", "")) + target.getLevel();
            amount = (getExpToLevel(neededLevel) + getTotalExperience(target) - getExpToLevel(target.getLevel()));
            setTotalExperience(target, 0);
        } else {

            amount = Long.parseLong(lowerCase);
        }
        amount += getTotalExperience(target);
        if (amount > 2147483647L) {
            amount = 2147483647L;
        }
        if (amount < 0L) {
            amount = 0L;
        }
        setTotalExperience(target, (int) amount);
    }
    public static void setTotalExperience(@NotNull Player player, int exp) throws IllegalArgumentException {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }

        player.setExp(0.0F);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;
        while (amount > 0) {
            int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                player.giveExp(expToLevel);
                continue;
            }
            amount += expToLevel;
            player.giveExp(amount);
            amount = 0;
        }
    }
    private static int getExpAtLevel(@NotNull Player player) {
        return getExpAtLevel(player.getLevel());
    }
    public static int getExpAtLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        }
        if (level <= 30) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }
    public static int getExpToLevel(int level) {
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }
    public static int getTotalExperience(@NotNull Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }
}
