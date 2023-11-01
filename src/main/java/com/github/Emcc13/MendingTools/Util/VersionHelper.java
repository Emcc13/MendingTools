package com.github.Emcc13.MendingTools.Util;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class VersionHelper {
    private static final String NMS_VERSION = getNmsVersion();
    private static final int V1_13 = 1130;

    private static final int V1_14 = 1140;

    private static final int V1_16 = 1160;

    private static final int V1_16_5 = 1165;

    private static final int V1_12 = 1120;

    private static final int CURRENT_VERSION = getCurrentVersion();

    private static final boolean IS_PAPER = checkPaper();
    public static final boolean IS_COMPONENT = (IS_PAPER && CURRENT_VERSION >= 1165);
    public static final boolean IS_ITEM_LEGACY = (CURRENT_VERSION < 1130);
    public static final boolean IS_PDC_VERSION = (CURRENT_VERSION >= 1140);
    public static final boolean IS_SKULL_OWNER_LEGACY = (CURRENT_VERSION <= 1120);
    public static final boolean IS_CUSTOM_MODEL_DATA = (CURRENT_VERSION >= 1140);

    public static final boolean IS_HEX_VERSION = (CURRENT_VERSION >= 1160);

    private static List<InventoryType> CHEST_INVENTORY_TYPES = null;

    private static List<InventoryType> VALID_INVENTORY_TYPES = null;

    private static List<InventoryType> getChestInventoryTypes() {
        if (CHEST_INVENTORY_TYPES != null) return CHEST_INVENTORY_TYPES;

        if (CURRENT_VERSION >= 1140) {
            CHEST_INVENTORY_TYPES = Stream.of(InventoryType.BARREL, InventoryType.CHEST, InventoryType.CRAFTING, InventoryType.CREATIVE, InventoryType.ENDER_CHEST, InventoryType.LECTERN, InventoryType.MERCHANT, InventoryType.SHULKER_BOX)
                    .collect(Collectors.toList());
            return CHEST_INVENTORY_TYPES;
        }

        CHEST_INVENTORY_TYPES = Stream.of(InventoryType.CHEST, InventoryType.CRAFTING, InventoryType.CREATIVE, InventoryType.ENDER_CHEST, InventoryType.MERCHANT, InventoryType.SHULKER_BOX)
                .collect(Collectors.toList());
        return CHEST_INVENTORY_TYPES;
    }

    public static List<InventoryType> getValidInventoryTypes() {
        if (VALID_INVENTORY_TYPES != null) return VALID_INVENTORY_TYPES;

        List<InventoryType> chestInventoryTypes = getChestInventoryTypes();
        List<InventoryType> validInventoryTypes = new ArrayList<>();

        for (InventoryType inventoryType : InventoryType.values()) {
            if (inventoryType == InventoryType.CHEST || !chestInventoryTypes.contains(inventoryType)) {
                validInventoryTypes.add(inventoryType);
            }
        }
        VALID_INVENTORY_TYPES = validInventoryTypes;
        return VALID_INVENTORY_TYPES;
    }
    private static boolean checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
    private static int getCurrentVersion() {
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());

        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            String patch = matcher.group("patch");
            if (patch == null) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append(patch.replace(".", ""));
            }

        }

        Integer version = Ints.tryParse(stringBuilder.toString());
        if (version == null) throw new RuntimeException("Could not retrieve server version!");

        return version.intValue();
    }

    private static String getNmsVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    public static Class<?> craftClass(@NotNull String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + name);
    }
}
