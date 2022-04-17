package com.github.Emcc13.MendingTools.Config;

import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.configuration.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public enum BaseConfig_EN implements ConfigInterface {
    perm_command_reload("mt.reload"),
    perm_command_blueprints("mt.blueprints"),
    perm_command_newMendingTool("mt.newMendingTool"),
    perm_command_tools("mt.tools"),
    perm_command_restoreTool("mt.restoreTool"),
    perm_command_upgradeTool("mt.upgradeTool"),
    perm_command_deleteTool("mt.deleteTool"),
    perm_command_transferTool("mt.transferTool"),
    perm_command_mendingtools("mendingtools"),
    perm_keep_inventory("mt.dummy_perm.keep_inv"),
    altColor("&"),
    language("de"),
    mendingToolBlueprintFile("blueprints.xml"),
    ;

    public enum EN implements ConfigInterface {
        languageConf_noPermission(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "You don't have permission for this command!");
            }});
        }}),
        languageConf_error_db(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Failed to perform database action!");
            }});
        }}),
        languageConf_error_noSuchTool(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "There is no such tool %ID%!");
            }});
        }}),
        languageConf_error_hasNoTools(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "%PLAYER% has no tools!");
            }});
        }}),
        languageConf_error_noSuchEnchantment(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "The tool does not have the enchantment: %ENCH%");
            }});
        }}),
        languageConf_error_notPlayed(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "%PLAYER% has not played on the server! Please check this and perform the action by hand if necessary!");
            }});
        }}),
        languageConf_error_loadOfflinePlayer(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Failed to load offline Player: %PLAYER%! Please check this and perform the action by hand if necessary!");
            }});
        }}),
        languageConf_error_loadBlueprint(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Failed to load blueprint for %ID%. Please report this issue or check the config.");
            }});
        }}),
        languageConf_error_notEnoughMoney(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "%PLAYER% has not enough money; required: %MONEY%.");
            }});
        }}),
        languageConf_error_removingItem(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Failed to remove tool %ID% from player %PLAYER%!");
            }});
        }}),
        languageConf_error_targetLevelBelow(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "The desired Level is lower than the current level!");
            }});
        }}),
        languageConf_hint_deleteTool(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% <tool id>");
            }});
        }}),
        languageConf_hint_restoreTool(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% <tool id>");
            }});
        }}),
        languageConf_hint_toolNotBroken(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Tool %ID% is not broken!");
            }});
        }}),
        languageConf_hint_toolNew(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% <blueprint id> <player name>");
            }});
        }}),
        languageConf_hint_tools(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% [all | player name | tool id]");
            }});
        }}),
        languageConf_hint_transferTool(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% <tool id> <player name>");
            }});
        }}),
        languageConf_hint_upgradeTool(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% <tool id> <enchantment> <new level>");
            }});
        }}),
        languageConf_hint_mendingtools(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "/%COMMAND% [tools, blueprints, delete, restore, new, transfer, upgrade] <subcommand " +
                        "specific arguments>");
            }});
        }}),

        languageConf_text_nextBook(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "Next Book");
                put("runcommand", "/%COMMAND% all %ID%");
            }});
        }}),
        languageConf_text_upgrade(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&o&7Upgrade");
            }});
        }}),
        languageConf_text_player(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&nPlayer");
            }});
        }}),
        languageConf_text_broken(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&oBroken");
            }});
        }}),
        languageConf_text_intact(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&oIntact");
            }});
        }}),
        languageConf_text_restore(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "&4Restore");
            }});
        }}),
        languageConf_text_noTools(new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>(){{
                put("text", "You have no mending tools.");
            }});
        }}),
        ;
        public final List<Map<String, String>> value;

        EN(List<Map<String, String>> value) {
            this.value = value;
        }

        public Object value() {
            return this.value;
        }

        public String key() {
            return this.name().replace('_', '.');
        }
    }

    public final Object value;

    BaseConfig_EN(Object value) {
        this.value = value;
    }

    public Object value() {
        return this.value;
    }

    public String key() {
        return this.name().replace('_', '.');
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

        char altColor_char = (char) cachedConfig.get(altColor.key());
        config.addDefaults(cachedConfig);

        switch ((String) cachedConfig.get(language.key())) {
            case "de":
                cachedConfig.putAll(parse_language(DE.class, config, altColor_char));
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
            for (Object obj_ : components) {
                Map<String, String> comp = (Map<String, String>) obj_;
                TextComponent ntc = new TextComponent(
                        ChatColor.translateAlternateColorCodes(altColor_char, comp.get("text")));
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
                if (comp.containsKey("clipboard")){
                    ntc.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                            comp.get("clipboard")));
                }
                if (comp.containsKey("openurl")) {
                    ntc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            comp.get("openurl")));
                }
                tcs.add(ntc);
            }
            cachedConfig.put(key, tcs);
        }
        return cachedConfig;
    }
}
