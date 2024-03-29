# MendingTools v0.9.1 for API 1.18

A plugin to manage tools with a mending enchantment.

## Commands

- mendingtools
    - perm: none specific, see subcommands
    - subcommands:
        - reload -> mt_reload
        - blueprints -> mt_blueprints
        - tools -> mt_tools
        - new -> mt_new_mending_tool
        - restore -> mt_restore_tool
        - upgrade -> mt_upgrade_tool
        - delete -> mt_delete_tool
        - transfer -> mt_transfer_tool
        - confirm -> mt_confirm
        - version -> mt_version
- mt_reload
    - perm: mt.reload
    - reload config/blueprints
- mt_blueprints
    - perm: mt.blueprints
    - open a book with all blueprints
- mt_tools
    - perm: mt.tools
    - open a book with the own tools
    - arg: book <book number> -> open 2nd/3rd... book with the own tools
    - arg: all - requires mt.tools_team perm -> open book with all tools
    - arg: playername <book number> - requires mt.tools_team perm -> open book with all tools of player
    - arg: id <tool id> - requires mt.tools_team perm if the tool is not owned by CommandSender -> show page of tool with id
- mt_new_mending_tool
    - perm: mt.newMendingTool
    - args: blueprint-id player
    - creates a tool and adds it to players inventory
- mt_restore_tool
    - perm: mt.restoreTool
    - restore a broken tool with 0 durability
- mt_upgrade_tool
    - perm: mt.upgradeTool
    - args: id enchantment level
    - upgrade tools enchantment to target level
- mt_delete_tool
    - perm: mt.deleteTool
    - args: id
    - delete tool from database and remove it from player inventory/enderchest
- mt_transfer_tool
    - perm: mt.transferTool
    - args: id player
    - transfer item to player, removes item from previous owner inventory and adds it to new player inventory
- mt_rename
    - perm: mt.renameTool
    - args: new name
    - set the display name of item in main hand
- mt_confirm
  - perm: mt.confirm
  - args: "config key for chat message" "message arguments"... (text_to_replace=text_to_insert)
  - confirm upgrade of enchantment
- mt_version
  - perm: mt.version
  - args: None
  - getting the plugin version

## Events

- onPlayerJoin
    - scan player inventory/ender chest to check for missing items / new items not added by this plugin
    - looking for item stacks containing mending enchantment and player name as part of the lore and tries to find the
      respective blueprint
    - every missing tool is marked as broken
- onItemBreak
    - mark tool as broken if it was a registered tool
- onPlayerDeath
    - mark all "lost" tools as broken if the player has not the keep inventory permission (see conf) and remove
      respective item stacks from drop list
- onInventoryClick, onInventoryDrag, onPlayerDropItem
    - prevent dropping, moving into any item container (ender chest and any non-persistent container like anvil is ok)
      of any mending tool

## Config

- tabComplete: autocompletion hints for every subcommand
- language: de (default) & en
- altColor: Chat color char (default: '&')
- mendingToolBlueprintFile: filename for the blueprints xml
- perm: permissions
    - mending tools command permissions
    - keep_inventory: permission for keeping the inventory of any other plugin (default: mt.dummy_perm.keep_inv)
- option:
  - restoreTool:
    - durability: durability of a tool after it has been restored
- bookButton:
  - configurations to display/handle "buttons" in books
- languageConf: changed by language setting
    - keys for TextComponents:
        - text -> shown text
        - showtext -> hover event
        - runcommand -> click event, execute command
        - suggestcommand -> click event, open chat and suggest command
        - clipboard -> copy to clipboard
        - openurl -> click event, open url


### LanguageConf

- prefix: Prefix for chat messages
- noPermission: command executed without permission
- error:
    - db: problem with the database
    - noSuchTool: tool with given id does not exist
    - hasNoTools: player has no tools to show
    - noSuchEnchantment: minecraft has no enchantment with given name
    - notPlayed: player has not played on the server yet
    - loadOfflinePlayer: failed to load offline player
    - loadBlueprint: blueprint of given tool could not be found in the blueprints config
    - notEnoughMoney: cannot restore / upgrade tool because the player has not enough money
    - removingItem: failed to remove item from players inventory
    - targetLevelBelow: requested to 'upgrade' enchantment, but given level is below current level
- hint: command hints
- text:
    - nextBook: text to switch to next book (clickable)
    - player: label where the player name is placed
    - broken: short text to label that the item is broken
    - intact: short text to label that the item is intact
    - restore: short text to restore tool (clickable)
    - noTools: (longer) text shown when a player has no tools
    - repairs: short text to label how often a tool has been repaired
    - mendingToolID: short text to label die tool ID
    - blueprintID: short text to label the blueprint ID

### BookButton

- upgrade:
    - command: chat message with clickable option to run a command
    - confirm: confirm command with arguments to send confirm message

## Blueprint Config

- root node: blueprints
    - blueprint: id, name, material
        - enchantment: name, level, maxlevel
            - money: money to upgrade; allowed args/functions:
                - %LEVEL%
                - %any enchantment% (gets replaced by the level if the respective tool has the enchantment)
                - sign
                - sin
                - cos
                - tan
            - command: command(s) performed when upgraded; allowed arguments:
                - %PLAYER%
                - %LEVEL%
                - %ID% (tool id)
                - %MONEY%
                - %BPNAME%
                - %BPID%
                - %RESTORES%
                - %any enchantment% (gets replaced by the level if the respective tool has the enchantment)
            - command ...
        - money: money to restore tool; allowed args/functions:
            - %LEVEL%
            - %RESTORES%
            - %#ENCH% (number enchantments including mending)
            - %any enchantment% (gets replaced by the level if the respective tool has the enchantment)
            - sign
            - sin
            - cos
            - tan
        - command: command(s) performed when restored; allowed arguments:
            - %PLAYER%
            - %ID% (tool id)
            - %MONEY%
            - %BPNAME%
            - %BPID%
            - %RESTORES%
        - command ...

general behaviour for money equations:

- any other char than a-zA-Z%# may throw an exception
- any other argument/function will result in a value of 0 for that term


## Database

SQLite3 database with following tables:

- CREATE TABLE IF NOT EXISTS Tool (ID INTEGER PRIMARY KEY AUTOINCREMENT, BlueprintID INTEGER, Material STRING, UUID
  STRING, Broken INTEGER, Restores INTEGER);
- CREATE TABLE IF NOT EXISTS Enchantments (ID INTEGER, Name STRING, Level INTEGER, FOREIGN KEY(ID) REFERENCES Tool(ID),
  PRIMARY KEY(ID, Name));

config:

- PRAGMA foreign_key = ON;
- filename: MendingTools.db