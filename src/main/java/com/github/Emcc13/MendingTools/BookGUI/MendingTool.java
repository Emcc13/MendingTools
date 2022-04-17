package com.github.Emcc13.MendingTools.BookGUI;

import com.github.Emcc13.MendingTools.Commands.MendingToolsCMD;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class MendingTool {
    private Long id;
    private Integer blueprintID;
    private String material;
    private String uuid;
    private boolean broken;
    private Map<String, Integer> enchantments;
    private Integer restores;

    public MendingTool(Long id, Integer blueprintID, String material, boolean broken, int restores, String uuid) {
        this.id = id;
        this.blueprintID = blueprintID;
        this.material = material;
        this.uuid = uuid;
        this.broken = broken;
        this.restores = restores;
        this.enchantments = new HashMap<String, Integer>();
    }

    public MendingTool(Long id, Integer blueprintID, String material, boolean broken, int restores, String uuid, Map<String, Integer> enchantments) {
        this(id, blueprintID, material, broken, restores, uuid);
        this.enchantments = enchantments;
    }

    public long getID() {
        return this.id;
    }

    public boolean isBroken() {
        return this.broken;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getMaterial() {
        return this.material;
    }

    public Integer getEnchantmentLevel(String enchantment) {
        return enchantments.get(enchantment);
    }

    public Integer getRestores(){
        return this.restores;
    }

    public Map<String, Integer> getEnchantments(){
        return this.enchantments;
    }

    public void addEnchantment(String material, Integer level) {
        this.enchantments.put(material, level);
    }

    public Integer getBlueprintID() {
        return this.blueprintID;
    }

    public BaseComponent[] asPage() {
        return asPage_(false);
    }

    public BaseComponent[] asPage_(boolean with_uuid) {
        List<BaseComponent> result = new LinkedList<BaseComponent>();
        MendingBlueprint blueprint = MendingToolsMain.getInstance().getBlueprintConfig().
                getBlueprints().get(this.blueprintID);
        TextComponent tc = new TextComponent((blueprint!=null?blueprint.getName():this.material)+"\n");
        tc.setBold(true);
//        tc.setUnderlined(true);
        result.add(tc);

        Map<String, Object> conf = MendingToolsMain.getInstance().getCachedConfig();
        tc = new TextComponent("ID: " + id + (blueprint!=null?"  |  BPID: " + blueprint.getID():"") + "\n");
        tc.setItalic(true);
        result.add(tc);
        tc = broken?((List<TextComponent>)conf.get(BaseConfig_EN.EN.languageConf_text_broken.key())).get(0).duplicate():
                ((List<TextComponent>)conf.get(BaseConfig_EN.EN.languageConf_text_intact.key())).get(0).duplicate();
        tc.addExtra(new TextComponent("  |  R: " + this.restores + "\n"));
        result.add(tc);
        int lines = 4;
        if (with_uuid) {
            tc = ((List<TextComponent>)conf.get(BaseConfig_EN.EN.languageConf_text_player.key())).get(0).duplicate();
//            tc.setUnderlined(true);
            result.add(tc);
            tc = new TextComponent("\n  " + this.nameForUUID(this.uuid) + "\n");
            result.add(tc);
            lines += 2;
        }
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            tc = new TextComponent(entry.getKey() + ":");
            tc.setUnderlined(true);
            result.add(tc);
            tc = new TextComponent("   " + entry.getValue());
            result.add(tc);
            lines++;
            if (blueprint!=null && blueprint.isUpgradeable(entry.getKey(), entry.getValue())) {
                tc = new TextComponent("\n     ");
                tc.addExtra(((List<TextComponent>)conf.get(BaseConfig_EN.EN.languageConf_text_upgrade.key())).get(0).duplicate());
                tc.addExtra("\n");
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
//                        "/" + mtUpgradeTool.COMMAND + " " + id + " " + entry.getKey() + " " + (entry.getValue() + 1)));
                        "/" + MendingToolsCMD.COMMAND + " upgrade " + id + " " + entry.getKey() + " " + (entry.getValue() + 1)));
                tc.setColor(ChatColor.GRAY);
//                tc.setItalic(true);
                lines++;
            } else {
                tc = new TextComponent("\n");
            }
            result.add(tc);
        }
        if (broken && (blueprint!=null || with_uuid)) {
            while (lines<13){
                result.add(new TextComponent("\n"));
                lines++;
            }
            tc = new TextComponent("-- ");
            tc.addExtra(((List<TextComponent>)conf.get(BaseConfig_EN.EN.languageConf_text_restore.key())).get(0).duplicate());
            tc.addExtra(new TextComponent(" --"));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
//                    "/" + mtRestoreTool.COMMAND + " " + this.id));
                    "/" + MendingToolsCMD.COMMAND + " restore " + this.id));
            result.add(tc);
        }
        return result.toArray(new BaseComponent[]{});
    }

    public ItemStack getItemStack(String playerName) {
        ItemStack result = new ItemStack(Material.getMaterial(material));
        ItemMeta im = result.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new LinkedList<>();
        lore.add(playerName);
        im.setLore(lore);
        NamespacedKey key = MendingToolsMain.getInstance().getNBT_key();
        im.getPersistentDataContainer().set(key, PersistentDataType.LONG, id);
        Damageable damage = (Damageable) im;
        damage.setDamage(result.getData().getItemType().getMaxDurability()-1);
        result.setItemMeta(im);
        Enchantment ench;
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            ench = Enchantment.getByKey(NamespacedKey.minecraft(entry.getKey()));
            if (ench == null)
                continue;
            result.addUnsafeEnchantment(ench, entry.getValue());
        }
        return result;
    }

    private String nameForUUID(String uuid) {
        UUID uuid_ = UUID.fromString(uuid);
        Player p = Bukkit.getServer().getPlayer(uuid_);
        if (p != null)
            return p.getName();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid_);
        if (offlinePlayer != null)
            return offlinePlayer.getName();
        return uuid;
    }
}
