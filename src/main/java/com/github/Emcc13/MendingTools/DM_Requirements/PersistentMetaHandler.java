package com.github.Emcc13.MendingTools.DM_Requirements;

import com.github.Emcc13.MendingToolsMain;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
public class PersistentMetaHandler
{
    @Nullable
    public PersistentDataType<?, ?> getSupportedType(@NotNull String name) {
        switch (name.toUpperCase(Locale.ROOT)) {
            case "DOUBLE":
                return PersistentDataType.DOUBLE;
            case "INTEGER":
            case "LONG":
                return PersistentDataType.LONG;
            case "STRING":
            case "BOOLEAN":
                return PersistentDataType.STRING;
        }
        return null;
    }
    @NotNull
    private NamespacedKey getKey(@NotNull String key) {
        NamespacedKey namespacedKey;
        if (key.contains(":")) {
            String[] split = key.split(":", 2);
            namespacedKey = new NamespacedKey(split[0], split[1]);
        } else {
            namespacedKey = new NamespacedKey((Plugin) MendingToolsMain.getInstance(), key);
        }

        return namespacedKey;
    }

    @Nullable
    public String getMeta(@NotNull Player player, @NotNull String key, @NotNull String typeName, @Nullable String defaultValue) {
        NamespacedKey namespacedKey = getKey(key);
        PersistentDataType<?, ?> type = getSupportedType(typeName);

        if (type == null) {
            return defaultValue;
        }

        Object result = player.getPersistentDataContainer().get(namespacedKey, type);
        if (result == null) {
            return defaultValue;
        }

        return result.toString();
    }

    public boolean setMeta(@NotNull Player player, @NotNull String input) throws NumberFormatException {
        String[] args = input.split(" ", 4);

        if (args.length < 4) {
            return false;
        }

        DataAction action = DataAction.getByName(args[0]);
        if (action == null) {
            return false;
        }

        PersistentDataType<?, ?> type = getSupportedType(args[2]);
        if (type == null) {
            return false;
        }

        return setMeta(player, getKey(args[1]), type, action, args[3]);
    }

    public boolean setMeta(@NotNull Player player, @NotNull NamespacedKey key, @NotNull PersistentDataType type, @NotNull DataAction action, @NotNull String value) throws NumberFormatException {
        boolean currentValueSwitch;
        Object currentValueAdd;
        long toAddLong;
        Object currentValueSubtract;
        long toSubLong;
        if (value.equalsIgnoreCase("null")) {
            player.getPersistentDataContainer().remove(key);
            return true;
        }

        switch (action) {
            case SET:
                if (type == PersistentDataType.STRING) {
                    player.getPersistentDataContainer().set(key, type, value);
                    return true;
                }

                if (type == PersistentDataType.DOUBLE) {
                    player.getPersistentDataContainer().set(key, type, Double.valueOf(Double.parseDouble(value)));
                    return true;
                }

                if (type == PersistentDataType.LONG) {
                    player.getPersistentDataContainer().set(key, type, Long.valueOf(Long.parseLong(value)));
                    return true;
                }

                return false;

            case REMOVE:
                player.getPersistentDataContainer().remove(key);
                return true;

            case SWITCH:
                currentValueSwitch = Boolean.parseBoolean((String)player
                        .getPersistentDataContainer().getOrDefault(key, type, value));

                player.getPersistentDataContainer().set(key, type, String.valueOf(!currentValueSwitch));
                return true;

            case ADD:
                if (type == PersistentDataType.STRING) {
                    return false;
                }

                currentValueAdd = player.getPersistentDataContainer().getOrDefault(key, type, Integer.valueOf(0));

                if (type == PersistentDataType.DOUBLE) {
                    double toAdd = Double.parseDouble(currentValueAdd.toString()) + Double.parseDouble(value);
                    player.getPersistentDataContainer().set(key, type, Double.valueOf(toAdd));
                    return true;
                }

                toAddLong = Long.parseLong(currentValueAdd.toString()) + Long.parseLong(value);
                player.getPersistentDataContainer().set(key, type, Long.valueOf(toAddLong));
                return true;

            case SUBTRACT:
                if (type == PersistentDataType.STRING) {
                    return false;
                }

                currentValueSubtract = player.getPersistentDataContainer().getOrDefault(key, type, Integer.valueOf(0));

                if (type == PersistentDataType.DOUBLE) {
                    double toSub = ((Double)currentValueSubtract).doubleValue() - Double.parseDouble(value);
                    player.getPersistentDataContainer().set(key, type, Double.valueOf(toSub));
                    return true;
                }

                toSubLong = Long.parseLong(currentValueSubtract.toString()) - Long.parseLong(value);
                player.getPersistentDataContainer().set(key, type, Long.valueOf(toSubLong));
                return true;
        }

        return false;
    }

    public enum DataAction {
        SET, REMOVE, ADD, SUBTRACT, SWITCH;

        static final Map<String, DataAction> BY_NAME = (Map<String, DataAction>)Arrays.<DataAction>stream(values())
                .collect(Collectors.toMap(Enum::name, Function.identity()));

        static {

        }

        @Nullable
        public static DataAction getByName(@NotNull String name) {
            return BY_NAME.get(name.toUpperCase(Locale.ROOT));
        }
    }
}
