package com.github.Emcc13.MendingTools.Config;

import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public enum TranslateConf implements ConfigInterface {
    ;
    public static final String languageConf_material = "languageMapping.material.";
    public static final String languageConf_enchantment = "languageMapping.enchantment.";

    public final List<Map<String, String>> value;
    private final String key_;

    TranslateConf(List<Map<String, String>> value) {
        this.value = value;
        this.key_ = this.name().replace('_', '.');
    }

    TranslateConf(String key, List<Map<String, String>> value) {
        this.value = value;
        this.key_ = key;
    }

    public List<Map<String, String>> value() {
        return this.value;
    }

    public String key() {
        return this.key_;
    }

    public static Map<String, List<TextComponent>> getConfig(Map<String, Object> cachedConfig) {
        Map<String, List<TextComponent>> translations = new HashMap<>();
        File configFile;
        FileConfiguration config;

        String conf_file_name = (String) cachedConfig.get(BaseConfig_EN.language.key()) + ".yml";
        configFile = new File(MendingToolsMain.getInstance().getDataFolder().getAbsolutePath(), conf_file_name);
        try {
            if (!configFile.getParentFile().exists()) {
                if (!configFile.getParentFile().mkdir()) {
                    Bukkit.getLogger().log(Level.SEVERE, "[MT Error] Failed to create language folder.");
                    return translations;
                }
                if (!configFile.createNewFile()) {
                    Bukkit.getLogger().log(Level.SEVERE, "[MT Error] Failed to create language file.");
                    return translations;
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            if (configFile.exists())
                config.load(configFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return translations;
        }

        String key;
        List<Map<String, String>> default_conf;
        Object entry;
        char altColor_char = (char) cachedConfig.get(BaseConfig_EN.altColor.key());

        for (Material mat : Material.values()) {
            key = TranslateConf.languageConf_material + mat.name();
            default_conf = new ArrayList<Map<String, String>>() {{
                add(new HashMap<String, String>() {{
                    put("text", mat.name());
                }});
            }};
//            config.addDefault(key, yaml.dump(default_conf));
            config.addDefault(key, default_conf);
            entry = config.get(key);
            translations.put(key, entry != null ? format_conf((List) entry, altColor_char) :
                            new LinkedList<TextComponent>() {{
                                add(new TextComponent(mat.name()));
                            }});
        }
        for (Enchantment ench : Enchantment.values()) {
            key = TranslateConf.languageConf_enchantment + ench.getKey().getKey();
            default_conf = new ArrayList<Map<String, String>>() {{
                add(new HashMap<String, String>() {{
                    put("text", ench.getKey().getKey());
                }});
            }};
//            config.addDefault(key, yaml.dump(default_conf));
            config.addDefault(key, default_conf);
            entry = config.get(key);
            translations.put(key, entry != null ? format_conf((List) entry, altColor_char) :
                    new LinkedList<TextComponent>() {{
                        add(new TextComponent(ench.getKey().getKey()));
                    }});
        }
        config.options().copyDefaults(true);

        switch ((String) cachedConfig.get(BaseConfig_EN.language.key())) {
            case "de":
                translations.putAll(parse_language(DE_language.class, config, altColor_char));
                break;
            default:
                translations.putAll(parse_language(TranslateConf.class, config, altColor_char));
                break;
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return translations;
    }

    private static Map<String, List<TextComponent>> parse_language(
            Class language_conf, Configuration config, char altColor_char) {
//        Yaml yaml = new Yaml();
        Map<String, List<TextComponent>> cachedConfig = new HashMap<>();
        for (Object entry_ : language_conf.getEnumConstants()) {
            ConfigInterface entry = (ConfigInterface) entry_;
            String key = entry.key();

//            String default_value = yaml.dump(entry.value());
//            String value = (String) config.get(key, default_value);
            List value = (List) config.get(key, entry.value());
            config.addDefault(key, value);
//            List components = yaml.load(value);

            List<TextComponent> tcs = new LinkedList<>();
            try {
                format_object(value, altColor_char);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "[MT] Failed to load config for key: " + key);
                continue;
            }
            cachedConfig.put(key, tcs);
        }
        return cachedConfig;
    }

    private static List<TextComponent> format_conf(List value, char altColor_char){
        List<TextComponent> tcs = new LinkedList<>();
        TextComponent ntc;
        for (Object obj_ : value) {
            ntc = format_object(obj_, altColor_char);
            tcs.add(ntc);
        }
        return tcs;
    }

    private static TextComponent format_object(Object obj_, char altColor_char) {
        Map<String, String> comp;
        TextComponent ntc;
        comp = (Map<String, String>) obj_;
        ntc = new TextComponent(ChatColor.translateAlternateColorCodes(altColor_char, comp.get("text")));

        if (comp.containsKey("showtext")) {
            ntc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text(ChatColor.translateAlternateColorCodes(altColor_char, comp.get("showtext")))));
        }
        if (comp.containsKey("runcommand")) {
            ntc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    comp.get("runcommand")));
        }
        if (comp.containsKey("suggestcommand")) {
            ntc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    comp.get("suggestcommand")));
        }
        if (comp.containsKey("clipboard")) {
            ntc.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                    comp.get("clipboard")));
        }
        if (comp.containsKey("openurl")) {
            ntc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    comp.get("openurl")));
        }
        return ntc;
    }
}
