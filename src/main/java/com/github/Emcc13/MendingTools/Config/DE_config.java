package com.github.Emcc13.MendingTools.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DE_config implements ConfigInterface {
    languageConf_noPermission(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Du hast nicht die nötigen Rechte für diesen Befehl!");
        }});
    }}),
    languageConf_error_db(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Datenbankaktion fehlgeschlagen!");
        }});
    }}),
    languageConf_error_noSuchTool(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Es existiert kein Werkzeug mit der ID: %ID%!");
        }});
    }}),
    languageConf_error_hasNoTools(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Der Spieler %PLAYER% besitzt keine Werkzeuge!");
        }});
    }}),
    languageConf_error_noSuchEnchantment(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Das Werkzeug hat nicht die Verzauberung: %ENCH%");
        }});
    }}),
    languageConf_error_notPlayed(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% %PLAYER% hat bisher noch nicht auf dem Server gespielt! Bitte dies überprüfen und die Aktion manuell ausführen!");
        }});
    }}),
    languageConf_error_loadOfflinePlayer(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Laden des offline Spielers %PLAYER% ist fehlgeschlagen! Bitte dies überprüfen und die Aktion manuell ausführen!");
        }});
    }}),
    languageConf_error_loadBlueprint(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Die Blaupause %ID% konnte nicht geladen werden. Bitte diesen Fehler melden oder die Konfiguration überprüfen.");
        }});
    }}),
    languageConf_error_notEnoughMoney(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% %PLAYER% hat nicht genügend Geld; benötigt: %MONEY%.");
        }});
    }}),
    languageConf_error_removingItem(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Das Entfernen des Werkzeugs %ID% von Spieler %PLAYER% ist fehlgeschlagen!");
        }});
    }}),
    languageConf_error_targetLevelBelow(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Die gewünschte Verzauberungsstufe ist niedriger als die Aktuelle!");
        }});
    }}),
    languageConf_error_toolHasNotEnchantment(new ArrayList<Map<String, String>>() {{
        add(new HashMap<String, String>() {{
            put("text", "%PREFIX% Das Werkzeug %ID% hat nicht die Verzauberung: %DISP-ENCH%!");
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
            put("text", "Das Werkzeug %ID% ist nicht kaputt!");
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
    languageConf_hint_renameTool(new ArrayList<Map<String, String>>() {{
        add(new HashMap<String, String>(){{
            put("text", "/%COMMAND% <new name>");
        }});
    }}),
    languageConf_hint_mendingtools(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "/%COMMAND% [tools, blueprints, delete, restore, new, transfer, upgrade, rename] <subcommand " +
                    "specific arguments>");
        }});
    }}),
    languageConf_hint_confirm(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "/%COMMAND% Konfiguration der Chat Nachricht");
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

    languageConf_text_nextBook(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "Nächstes Buch");
        }});
    }}),
    languageConf_text_player(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&nSpieler");
        }});
    }}),
    languageConf_text_broken(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&o&cKaputt");
        }});
    }}),
    languageConf_text_intact(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&o&eIntakt");
        }});
    }}),
    languageConf_text_restore(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&4Reparieren");
        }});
    }}),
    languageConf_text_noTools(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "Du besitzt derzeit keine Mending-Werkzeuge.");
        }});
    }}),
    languageConf_text_repairs(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "Reparaturen");
        }});
    }}),
    languageConf_text_mendingToolID(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "Werkzeug ID");
        }});
    }}),
    languageConf_text_blueprintID(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "Blaupausen ID");
        }});
    }}),
    languageConf_text_pageOfContentTitle(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&lInhaltsverzeichnis");
        }});
    }}),

    bookButton_upgrade_command(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "%PREFIX% Möchtest du für dein Tool %ID% die Verzauberung %DISP-ENCH% von Stufe " +
                    "%CURRLEVEL% auf Stufe %LEVEL% für %MONEY% Platin aufwerten?  ");
        }});
        add(new HashMap<String, String>(){{
            put("text", "[Ja]");
            put("runcommand", "/mt upgrade %ID% %ENCH% %LEVEL%");
        }});

    }}),
    bookButton_upgrade_confirm(new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("text", "&o&7Upgrade");
            put("runcommand", "/mt confirm "+DE_config.bookButton_upgrade_command.key()+
                    " $ID$=%ID% $LEVEL$=%LEVEL% $ENCH$=%ENCH% $CURRLEVEL$=%CURRLEVEL% $MONEY$=%MONEY% $DISP-ENCH$=%DISP-ENCH%");
        }});
    }})
    ;
    public final List<Map<String, String>> value;
    DE_config(List<Map<String, String>> value) {
        this.value = value;
    }
    public Object value(){
        return this.value;
    }
    public String key(){
        return this.name().replace('_','.');
    }
}
