package com.github.Emcc13.MendingTools.DM_Requirements.wrappers;

import java.util.List;

public class ItemWrapper {
    private String material = null;
    private String name = null;
    private String lore = null;
    private List<String> loreList = null;

    private short data = 0;
    private boolean hasData = false;
    private int customData = 0;
    private int amount = 1;

    private boolean strict = false;

    private boolean armor = false;

    private boolean offhand = false;
    private boolean nameContains = false;
    private boolean nameIgnoreCase = false;
    private boolean loreContains = false;
    private boolean loreIgnoreCase = false;

    public String getMaterial() {
        return this.material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLore() {
        return this.lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public List<String> getLoreList() {
        return this.loreList;
    }

    public void setLoreList(List<String> loreList) {
        this.loreList = loreList;
    }

    public short getData() {
        return this.data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public boolean hasData() {
        return this.hasData;
    }

    public void hasData(boolean hasData) {
        this.hasData = hasData;
    }

    public int getCustomData() {
        return this.customData;
    }

    public void setCustomData(int customData) {
        this.customData = customData;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean checkArmor() {
        return this.armor;
    }

    public void setArmor(boolean armor) {
        this.armor = armor;
    }

    public boolean checkOffhand() {
        return this.offhand;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }
    public boolean checkNameContains() {
        return this.nameContains;
    }

    public void setNameContains(boolean nameContains) {
        this.nameContains = nameContains;
    }

    public boolean checkNameIgnoreCase() {
        return this.nameIgnoreCase;
    }

    public void setNameIgnoreCase(boolean nameIgnoreCase) {
        this.nameIgnoreCase = nameIgnoreCase;
    }

    public boolean checkLoreContains() {
        return this.loreContains;
    }

    public void setLoreContains(boolean loreContains) {
        this.loreContains = loreContains;
    }

    public boolean checkLoreIgnoreCase() {
        return this.loreIgnoreCase;
    }

    public void setLoreIgnoreCase(boolean loreIgnoreCase) {
        this.loreIgnoreCase = loreIgnoreCase;
    }
}
