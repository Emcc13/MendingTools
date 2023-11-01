package com.github.Emcc13.MendingTools.BookGUI;

import com.github.Emcc13.MendingTools.Commands.MendingToolsCMD;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Config.TranslateConf;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.github.Emcc13.MendingTools.Util.Equationparser;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
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

    public Integer getRestores() {
        return this.restores;
    }

    public Map<String, Integer> getEnchantments() {
        return this.enchantments;
    }

    public void addEnchantment(String material, Integer level) {
        this.enchantments.put(material, level);
    }

    public Integer getBlueprintID() {
        return this.blueprintID;
    }

    public List<BaseComponent[]> asPage_Player() {
        List<BaseComponent[]> result = new LinkedList<>();
        List<BaseComponent> page = new LinkedList<>();

        MendingBlueprint blueprint = MendingToolsMain.getInstance().getBlueprintConfig().
                getBlueprints().get(this.blueprintID);
        Map<String, Object> conf = MendingToolsMain.getInstance().getCachedConfig();
        Map<String, List<TextComponent>> languageConf = MendingToolsMain.getInstance().getLanguageConfig();
//        Page 1
//        Blueprint Name
        page.addAll(bookBlueprint(blueprint));
//        owner of the mending tool (player name)
        page.addAll(bookOwner(conf));
//        MendingToolID
        page.addAll(bookToolId(conf));
//        #Repairs
        page.addAll(bookRepairs(conf));
//        Broken/Intact
        page.addAll(bookBrokenIntact(conf));
//        Restore Button
        page.addAll(bookRestoreButton(conf));
//        add page to list
        result.add(page.toArray(new BaseComponent[]{}));

//        Page 2
//        Enchantments
        page = bookEnchantments(blueprint, conf, languageConf);
        result.add(page.toArray(new BaseComponent[]{}));

        return result;
    }

    public List<BaseComponent[]> asPage_Teamler() {
        List<BaseComponent[]> result = new LinkedList<>();
        List<BaseComponent> page = new LinkedList<>();

        MendingBlueprint blueprint = MendingToolsMain.getInstance().getBlueprintConfig().
                getBlueprints().get(this.blueprintID);
        Map<String, Object> conf = MendingToolsMain.getInstance().getCachedConfig();
        Map<String, List<TextComponent>> languageConf = MendingToolsMain.getInstance().getLanguageConfig();
//        Page 1
//        Blueprint Name
        page.addAll(bookBlueprint(blueprint));
//        owner of the mending tool (player name)
        page.addAll(bookOwner(conf));
//        MendingToolID
        page.addAll(bookToolId(conf));
//        #Repairs
        page.addAll(bookRepairs(conf));
//        Broken/Intact
        page.addAll(bookBrokenIntact(conf));

//        Restore Button
        page.addAll(bookRestoreButton(conf));

//        Blueprint ID
        page.addAll(bookBlueprintID(blueprint, conf));
//        add page to list
        result.add(page.toArray(new BaseComponent[]{}));

//        Page 2
//        Enchantments
        page = bookEnchantments(blueprint, conf, languageConf);
        result.add(page.toArray(new BaseComponent[]{}));

        return result;
    }

    protected List<BaseComponent> bookBlueprint(MendingBlueprint blueprint) {
        TextComponent tc;
        if (blueprint != null)
            tc = new TextComponent(blueprint.getName() + "\n");
        else {
            tc = formatComponents(MendingToolsMain.getInstance().getLanguageConfig().get(TranslateConf.languageConf_material + this.material));
            tc.addExtra("\n");
        }
        tc.setBold(true);
        return new ArrayList<BaseComponent>() {{
            add(tc);
        }};
    }

    protected List<BaseComponent> bookOwner(Map<String, Object> conf) {
        return new ArrayList<BaseComponent>() {{
            add(((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_player.key())).get(0).duplicate());
            add(new TextComponent("\n  " + nameForUUID(uuid) + "\n"));
        }};
    }

    protected List<BaseComponent> bookToolId(Map<String, Object> conf) {
        TextComponent tc = ((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_mendingToolID.key())).get(0).duplicate();
        tc.addExtra(": " + id + "\n");
        tc.setItalic(true);
        return new ArrayList<BaseComponent>() {{
            add(tc);
        }};
    }

    protected List<BaseComponent> bookRepairs(Map<String, Object> conf) {
        TextComponent tc = ((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_repairs.key())).get(0).duplicate();
        tc.addExtra(": " + this.restores + "\n");
        return new ArrayList<BaseComponent>() {{
            add(tc);
        }};
    }

    protected List<BaseComponent> bookBrokenIntact(Map<String, Object> conf) {
        TextComponent tc = broken ? ((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_broken.key())).get(0).duplicate() :
                ((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_intact.key())).get(0).duplicate();
        tc.addExtra(new TextComponent("\n\n"));
        return new ArrayList<BaseComponent>() {{
            add(tc);
        }};
    }

    protected List<BaseComponent> bookRestoreButton(Map<String, Object> conf) {
        if (this.isBroken()) {
            TextComponent tc = new TextComponent("-- ");
            tc.addExtra(((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_restore.key())).get(0).duplicate());
            tc.addExtra(new TextComponent(" --"));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/" + MendingToolsCMD.COMMAND + " restore " + this.id));
            return new ArrayList<BaseComponent>() {{
                add(tc);
            }};
        } else {
            return new ArrayList<BaseComponent>();
        }
    }

    protected List<BaseComponent> bookBlueprintID(MendingBlueprint blueprint, Map<String, Object> conf) {
        TextComponent tc = new TextComponent("\n\n");
        tc.addExtra(((List<TextComponent>) conf.get(BaseConfig_EN.EN.languageConf_text_blueprintID.key())).get(0).duplicate());
        tc.addExtra(": " + (blueprint != null ? blueprint.getID() : "???") + "\n");
        return new ArrayList<BaseComponent>() {{
            add(tc);
        }};
    }

    protected List<BaseComponent> bookEnchantments(
            MendingBlueprint blueprint, Map<String, Object> conf, Map<String, List<TextComponent>> languageConf) {
        LinkedList<BaseComponent> result = new LinkedList<BaseComponent>();
        TextComponent tc;
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            List<TextComponent> lang_ench = languageConf.getOrDefault(
                    TranslateConf.languageConf_enchantment + entry.getKey().toLowerCase(),
                    new LinkedList<TextComponent>(){{add(new TextComponent(entry.getKey().substring(2)));}}
                    );
            tc = formatComponents(lang_ench);
            tc.addExtra(":\n");
            result.add(tc);
            tc = new TextComponent("  " + entry.getValue());
            result.add(tc);
            if (blueprint != null
                    && blueprint.isUpgradeable(entry.getKey(), entry.getValue())
                    && (blueprint.getEnchantment(entry.getKey()).getMoney() != null || blueprint.getEnchantment(entry.getKey()).getRequirements().size() > 0)
            ) {
                tc = new TextComponent(" ");
                tc.addExtra(formatComponents(
                        (List<TextComponent>) conf.get(BaseConfig_EN.EN.bookButton_upgrade_confirm.key()),
                        new Tuple<>("%ID%", String.valueOf(id)),
                        new Tuple<>("%LEVEL%", String.valueOf(entry.getValue() + 1)),
                        new Tuple<>("%ENCH%", entry.getKey()),
                        new Tuple<>("%DISP-ENCH%", lang_ench.get(0).getText()),
                        new Tuple<>("%CURRLEVEL%", String.valueOf(entry.getValue())),
                        new Tuple<>("%MONEY%", blueprint.getMoney() != null ? String.format("%,.0f",
                                calcMoney(blueprint, entry.getKey(), entry.getValue())) : "0")
                ));
                tc.addExtra("\n");
                tc.setColor(ChatColor.GRAY);
            } else {
                tc = new TextComponent("\n");
            }
            result.add(tc);
        }
        return result;
    }

    public ItemStack getItemStack(String playerName) {
        Map<String, Object> conf = MendingToolsMain.getInstance().getCachedConfig();
        ItemStack result = new ItemStack(Material.getMaterial(material));
        ItemMeta im = result.getItemMeta();
        List<String> lore = new LinkedList<>();

        NamespacedKey key = MendingToolsMain.getInstance().getNBT_key();
        im.getPersistentDataContainer().set(key, PersistentDataType.LONG, id);
        Damageable damage = (Damageable) im;
        damage.setDamage(result.getData().getItemType().getMaxDurability() - (Integer) conf.get(BaseConfig_EN.option_restoreTool_durability.key()));
        result.setItemMeta(im);
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            try {
                result.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(entry.getKey())), entry.getValue());
            }catch (Exception e){
                lore.add(ChatColor.translateAlternateColorCodes('&', entry.getKey())+" "+ MTListener.int2roman(entry.getValue()));
            }
        }
        if (lore.size()>0)
            lore.add("");
        lore.add(playerName);
        im = result.getItemMeta();
        im.setLore(lore);
        result.setItemMeta(im);
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

    protected TextComponent formatComponents(List<TextComponent> template, Tuple<String, String>... replacements) {
        TextComponent result = new TextComponent();
        for (TextComponent tc : template) {
            TextComponent copy = new TextComponent(formatString(tc.getText(), replacements));
            HoverEvent hover = tc.getHoverEvent();
            if (hover != null) {
                List<Content> contents = new LinkedList<>();
                for (Content content : hover.getContents()) {
                    contents.add(new Text(formatString(
                            ((String) ((Text) content).getValue()),
                            replacements
                    )));
                }
                copy.setHoverEvent(new HoverEvent(hover.getAction(), contents));
            }
            ClickEvent click = tc.getClickEvent();
            if (click != null) {
                copy.setClickEvent(new ClickEvent(click.getAction(),
                        formatString(click.getValue(), replacements)));
            }
            result.addExtra(copy);
        }
        return result;
    }

    private static String formatString(String template, Tuple<String, String>... replacements) {
        String result = template;
        for (Tuple<String, String> replacement : replacements) {
            result = result.replace(replacement.t1, replacement.t2);
        }
        return result;
    }

    private double calcMoney(MendingBlueprint blueprint, String enchantment, int currlevel) {
//        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment));
        MendingBlueprint.MTEnchantment blueprintEnch = blueprint.getEnchantment(enchantment);
        double moneyValue = 0;
        if (blueprintEnch.getMoney() == null)
            return moneyValue;
        int intermediateLevel = currlevel + 1;
//        if (!blueprint.upgradeAllowed(p, enchantment, intermediateLevel)) {
//            return moneyValue;
//        }
        Double dLevel = (double) intermediateLevel;
        moneyValue += Equationparser.eval(blueprintEnch.getMoney(), new HashMap<String, Double>() {{
            put("%LEVEL%", dLevel);
            for (Map.Entry<String, Integer> enchantment_ : enchantments.entrySet()) {
                put("%" + enchantment_.getKey() + "%", (double) enchantment_.getValue());
            }
        }});
        return moneyValue;
    }
}
