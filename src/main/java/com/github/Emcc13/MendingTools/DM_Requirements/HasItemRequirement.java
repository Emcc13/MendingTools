package com.github.Emcc13.MendingTools.DM_Requirements;

import com.github.Emcc13.MendingTools.DM_Requirements.wrappers.ItemWrapper;
import com.github.Emcc13.MendingTools.Util.StringUtils;
import com.github.Emcc13.MendingTools.Util.VersionHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HasItemRequirement extends Requirement {
    private final ItemWrapper wrapper;

    public HasItemRequirement(ItemWrapper wrapper, boolean invert) {
        this.wrapper = wrapper;
        this.invert = invert;
    }
    private final boolean invert;

    public boolean evaluate(MenuHolder holder) {
        String materialName = holder.setPlaceholders(this.wrapper.getMaterial()).toUpperCase();
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            return this.invert;
        }

        if (material == Material.AIR) return (this.invert == ((holder.getViewer().getInventory().firstEmpty() == -1)));

        ItemStack[] armor = this.wrapper.checkArmor() ? holder.getViewer().getInventory().getArmorContents() : null;
        ItemStack[] offHand = this.wrapper.checkOffhand() ? holder.getViewer().getInventory().getExtraContents() : null;
        ItemStack[] inventory = holder.getViewer().getInventory().getStorageContents();

        int total = 0;
        for (ItemStack itemToCheck : inventory) {
            if (isRequiredItem(itemToCheck, holder, material)) {
                total += itemToCheck.getAmount();
            }
        }
        if (offHand != null) {
            for (ItemStack itemToCheck : offHand) {
                if (isRequiredItem(itemToCheck, holder, material)) {
                    total += itemToCheck.getAmount();
                }
            }
        }
        if (armor != null) {
            for (ItemStack itemToCheck : armor) {
                if (isRequiredItem(itemToCheck, holder, material)) {
                    total += itemToCheck.getAmount();
                }
            }
        }
        return (this.invert == ((total < this.wrapper.getAmount())));
    }

    private boolean isRequiredItem(ItemStack itemToCheck, MenuHolder holder, Material material) {
        if (itemToCheck == null || itemToCheck.getType() == Material.AIR) return false;
        if (this.wrapper.getMaterial() != null && itemToCheck.getType() != material) return false;
        if (this.wrapper.hasData() && itemToCheck.getDurability() != this.wrapper.getData()) return false;

        ItemMeta metaToCheck = itemToCheck.getItemMeta();
        if (this.wrapper.isStrict()) {
            if (metaToCheck != null) {
                if (VersionHelper.IS_CUSTOM_MODEL_DATA &&
                        metaToCheck.hasCustomModelData()) return false;

                if (metaToCheck.hasLore()) return false;
                return !metaToCheck.hasDisplayName();
            }
        } else {

            if ((this.wrapper.getCustomData() != 0 || this.wrapper.getName() != null || this.wrapper.getLore() != null) && metaToCheck == null) {
                return false;
            }
            if (this.wrapper.getCustomData() != 0 &&
                    VersionHelper.IS_CUSTOM_MODEL_DATA) {
                if (!metaToCheck.hasCustomModelData()) return false;
                if (metaToCheck.getCustomModelData() != this.wrapper.getCustomData()) return false;

            }

            if (this.wrapper.getName() != null) {
                if (!metaToCheck.hasDisplayName()) return false;

                String name = StringUtils.color(holder.setPlaceholders(this.wrapper.getName()));
                String nameToCheck = StringUtils.color(holder.setPlaceholders(metaToCheck.getDisplayName()));

                if (this.wrapper.checkNameContains() && this.wrapper.checkNameIgnoreCase()) {
                    if (!StringUtils.containsIgnoreCase(nameToCheck, name)) return false;

                } else if (this.wrapper.checkNameContains()) {
                    if (!nameToCheck.contains(name)) return false;

                } else if (this.wrapper.checkNameIgnoreCase()) {
                    if (!nameToCheck.equalsIgnoreCase(name)) return false;

                } else if (!nameToCheck.equals(name)) {
                    return false;
                }
            }

            if (this.wrapper.getLoreList() != null) {
                List<String> loreX = metaToCheck.getLore();
                if (loreX == null) return false;

                Objects.requireNonNull(holder); String lore = this.wrapper.getLoreList().stream().map(holder::setPlaceholders).map(StringUtils::color).collect(Collectors.joining("&&"));
                Objects.requireNonNull(holder); String loreToCheck = loreX.stream().map(holder::setPlaceholders).map(StringUtils::color).collect(Collectors.joining("&&"));

                if (this.wrapper.checkLoreContains() && this.wrapper.checkLoreIgnoreCase()) {
                    if (!StringUtils.containsIgnoreCase(loreToCheck, lore)) return false;

                } else if (this.wrapper.checkLoreContains()) {
                    if (!loreToCheck.contains(lore)) return false;

                } else if (this.wrapper.checkLoreIgnoreCase()) {
                    if (!loreToCheck.equalsIgnoreCase(lore)) return false;

                } else if (!loreToCheck.equals(lore)) {
                    return false;
                }
            }

            if (this.wrapper.getLore() != null) {
                List<String> loreX = metaToCheck.getLore();
                if (loreX == null) return false;

                String lore = StringUtils.color(holder.setPlaceholders(this.wrapper.getLore()));
                Objects.requireNonNull(holder); String loreToCheck = loreX.stream().map(holder::setPlaceholders).map(StringUtils::color).collect(Collectors.joining("&&"));

                if (this.wrapper.checkLoreContains() && this.wrapper.checkLoreIgnoreCase()) {
                    return StringUtils.containsIgnoreCase(loreToCheck, lore);
                }
                if (this.wrapper.checkLoreContains()) {
                    return loreToCheck.contains(lore);
                }
                if (this.wrapper.checkLoreIgnoreCase()) {
                    return loreToCheck.equalsIgnoreCase(lore);
                }
                return loreToCheck.equals(lore);
            }
        }
        return true;
    }
}