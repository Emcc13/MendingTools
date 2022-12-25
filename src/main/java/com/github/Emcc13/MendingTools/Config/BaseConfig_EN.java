package com.github.Emcc13.MendingTools.Config;

import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.logging.Level;

public enum BaseConfig_EN implements ConfigInterface {
    perm_command_reload("mt.reload"),
    perm_command_blueprints("mt.blueprints"),
    perm_command_newMendingTool("mt.newMendingTool"),
    perm_command_tools("mt.tools"),
    perm_command_restoreTool("mt.restoreTool"),
    perm_command_upgradeTool("mt.upgradeTool"),
    perm_command_deleteTool("mt.deleteTool"),
    perm_command_transferTool("mt.transferTool"),
    perm_command_renameTool("mt.renameTool"),
    perm_command_confirm("mt.confirm"),
    perm_command_mendingtools("mendingtools"),
    perm_command_version("mt.version"),
    perm_keep_inventory("mt.dummy_perm.keep_inv"),
    altColor("&"),
    language("de"),
    mendingToolBlueprintFile("blueprints.xml"),

    option_restoreTool_durability(15),
    languageConf_prefix("[MT] "),
    ;

    public enum EN implements ConfigInterface {
        languageConf_noPermission(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% You don't have permission for this command!");
            }});
        }}),
        languageConf_error_db(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% Failed to perform database action!");
            }});
        }}),
        languageConf_error_noSuchTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% There is no such tool %ID%!");
            }});
        }}),
        languageConf_error_hasNoTools(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% %PLAYER% has no tools!");
            }});
        }}),
        languageConf_error_noSuchEnchantment(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% The tool does not have the enchantment: %ENCH%");
            }});
        }}),
        languageConf_error_notPlayed(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% %PLAYER% has not played on the server! Please check this and perform the action by hand if necessary!");
            }});
        }}),
        languageConf_error_loadOfflinePlayer(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% Failed to load offline Player: %PLAYER%! Please check this and perform the action by hand if necessary!");
            }});
        }}),
        languageConf_error_loadBlueprint(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% Failed to load blueprint for %ID%. Please report this issue or check the config.");
            }});
        }}),
        languageConf_error_notEnoughMoney(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% %PLAYER% has not enough money; required: %MONEY%.");
            }});
        }}),
        languageConf_error_removingItem(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% Failed to remove tool %ID% from player %PLAYER%!");
            }});
        }}),
        languageConf_error_targetLevelBelow(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "%PREFIX% The desired Level is lower than the current level!");
            }});
        }}),
        languageConf_hint_deleteTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <tool id>");
            }});
        }}),
        languageConf_hint_restoreTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <tool id>");
            }});
        }}),
        languageConf_hint_toolNotBroken(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "Tool %ID% is not broken!");
            }});
        }}),
        languageConf_hint_toolNew(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <blueprint id> <player name>");
            }});
        }}),
        languageConf_hint_tools(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% [all | player name [book number] | id <tool id> | book <book number>]");
            }});
        }}),
        languageConf_hint_transferTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <tool id> <player name>");
            }});
        }}),
        languageConf_hint_upgradeTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <tool id> <enchantment> <new level>");
            }});
        }}),
        languageConf_hint_renameTool(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% <new name>");
            }});
        }}),
        languageConf_hint_mendingtools(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "/%COMMAND% [tools, blueprints, delete, restore, new, transfer, upgrade, rename] <subcommand " +
                        "specific arguments>");
            }});
        }}),
        languageConf_hint_confirm(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% config key of text message");
            }});
        }}),
        languageConf_hint_version(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND%");
            }});
        }}),

        languageConf_text_version(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "%PREFIX% %VERSION%");
            }});
        }}),

        languageConf_text_nextBook(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "Next Book");
            }});
        }}),
        languageConf_text_player(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "&nPlayer");
            }});
        }}),
        languageConf_text_broken(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "&oBroken");
            }});
        }}),
        languageConf_text_intact(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "&oIntact");
            }});
        }}),
        languageConf_text_restore(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "&4Restore");
            }});
        }}),
        languageConf_text_noTools(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("text", "You have no mending tools.");
            }});
        }}),
        languageConf_text_repairs(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Repairs");
            }});
        }}),
        languageConf_text_mendingToolID(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Mending Tool ID");
            }});
        }}),
        languageConf_text_blueprintID(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Blueprint ID");
            }});
        }}),
        languageConf_text_pageOfContentTitle(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Page of Content");
            }});
        }}),

        bookButton_upgrade_command(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&%PREFIX% Are you sure you want to upgrade your tools %ID% enchantement %ENCH% from " +
                        "%CURRLEVEL% to %LEVEL% for %MONEY%?  ");
            }});
            add(new HashMap<String, String>(){{
                put("text", "[YES]");
                put("runcommand", "/mt upgrade %ID% %ENCH% %LEVEL%");
            }});

        }}),
        bookButton_upgrade_confirm(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&o&7Upgrade");
                put("runcommand", "/mt confirm "+BaseConfig_EN.EN.bookButton_upgrade_command.key()+
                        " $ID$=%ID% $LEVEL$=%LEVEL% $ENCH$=%ENCH% $CURRLEVEL$=%CURRLEVEL% $MONEY$=%MONEY%");
            }});
        }})

        ;
        public final List<Map<String, String>> value;
        private final String key_;

        EN(List<Map<String, String>> value) {
            this.value = value;
            this.key_ = this.name().replace('_', '.');
        }

        EN(String key, List<Map<String, String>> value) {
            this.value = value;
            this.key_ = key;
        }

        public Object value() {
            return this.value;
        }

        public String key(){
            return this.key_;
        }
    }

    public final Object value;
    private final String key_;

    BaseConfig_EN(Object value) {
        this.value = value;
        this.key_ = this.name().replace('_', '.');
    }

    public Object value() {
        return this.value;
    }

    public String key(){
        return this.key_;
    }

    public static Map<String, Object> getConfig(MendingToolsMain main) {
        Map<String, Object> cachedConfig = new HashMap<>();
        main.reloadConfig();
        Configuration config = main.getConfig();
        String key;
        Object value;
        for (BaseConfig_EN entry : BaseConfig_EN.values()) {
            key = entry.key();
            value = config.get(key);
            if (value == null) {
                value = entry.value;
            }
            cachedConfig.put(key, value);
        }
        cachedConfig.put(altColor.key(), ((String) cachedConfig.get(altColor.key())).charAt(0));
        try {
            cachedConfig.put(option_restoreTool_durability.key(), (Integer) cachedConfig.get(option_restoreTool_durability.key()));
        } catch (Exception e) {
            cachedConfig.put(option_restoreTool_durability.key(), (Integer) option_restoreTool_durability.value());
        }
        char altColor_char = (char) cachedConfig.get(altColor.key());
        config.addDefaults(cachedConfig);

        switch ((String) cachedConfig.get(language.key())) {
            case "de":
                cachedConfig.putAll(parse_language(DE_config.class, config, altColor_char));
                break;
            default:
                cachedConfig.putAll(parse_language(EN.class, config, altColor_char));
                break;
        }


        config.options().copyDefaults(true);
        main.saveConfig();
        return cachedConfig;
    }

    private static Map<String, Object> parse_language(Class language_conf, Configuration config, char altColor_char) {
        Yaml yaml = new Yaml();
        Map<String, Object> cachedConfig = new HashMap<>();
        for (Object entry_ : language_conf.getEnumConstants()) {
            ConfigInterface entry = (ConfigInterface) entry_;
            String key = entry.key();

            String default_value = yaml.dump(entry.value());
            String value = (String) config.get(key, default_value);
            config.addDefault(key, value);
            List components = yaml.load(value);

            List<TextComponent> tcs = new LinkedList<>();
            TextComponent ntc;
            for (Object obj_ : components) {
                try {
                    ntc = format_object(obj_, altColor_char);
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING,"[MT] Failed to load config for key: "+key);
                    continue;
                }
                tcs.add(ntc);
            }
            cachedConfig.put(key, tcs);
        }
        return cachedConfig;
    }

    private static TextComponent format_object(Object obj_, char altColor_char){
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
