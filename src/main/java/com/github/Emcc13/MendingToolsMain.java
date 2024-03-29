package com.github.Emcc13;

import com.github.Emcc13.MendingTools.Commands.*;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Config.BlueprintConfig;
import com.github.Emcc13.MendingTools.Config.TranslateConf;
import com.github.Emcc13.MendingTools.DM_Requirements.PersistentMetaHandler;
import com.github.Emcc13.MendingTools.Database.DBHandler;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.github.Emcc13.MendingTools.Util.UpdateChecker;
import com.github.Emcc13.MendingTools.Util.VaultHook;
import com.lishid.openinv.IOpenInv;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public class MendingToolsMain extends JavaPlugin {
    private static MendingToolsMain instance;
    private Map<String, Object> cachedConfig;
    private Map<String, List<TextComponent>> languageConfig;
    private BlueprintConfig blueprintConfig;
    private DBHandler dbhandler;
    private NamespacedKey NBT_key;
    private Economy economy;
    private IOpenInv openInv;
    private mtCommands generalCommand;

    private PersistentMetaHandler persistentMetaHandler;
    private VaultHook vaultHook;

    public Map<String, CommandExecutor> commands;

    public MendingToolsMain() {
        instance = this;
    }

    public void onEnable() {
        this.economy = Objects.requireNonNull(this.getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        this.openInv = (IOpenInv) this.getServer().getPluginManager().getPlugin("OpenInv");
        this.loadCachedConfig();
        this.createCommands();
        this.registerCommands();
        this.NBT_key = new NamespacedKey(this, "ID");
        this.addListener();

        this.persistentMetaHandler = new PersistentMetaHandler();
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            this.vaultHook = new VaultHook();
            if (this.vaultHook.hooked()) {
                this.getLogger().log(Level.INFO, "Successfully hooked into Vault!");
            }
        }

        try {
            String latestRelease = UpdateChecker.getLatestReleaseTitle();
            String[] partial = latestRelease.split(" ");
            if (partial.length < 3)
                return;
            latestRelease = String.copyValueOf(partial[2].toCharArray(), 1, partial[2].length() - 1);
            int[] current_version = Arrays
                    .stream(this.getDescription().getVersion().split("\\."))
                    .mapToInt(Integer::parseInt)
                    .toArray(),
                    latest_version = Arrays
                            .stream(latestRelease.split("\\."))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            for (int idx = 0; idx < Math.min(current_version.length, latest_version.length); idx++) {
                if (latest_version[idx] > current_version[idx]) {
                    this.getLogger().log(Level.SEVERE, "There is a new version available. " +
                            "Check out: https://github.com/Emcc13/MendingTools/releases/latest");
                    break;
                }
                if (current_version[idx] > latest_version[idx]) {
                    this.getLogger().log(Level.WARNING, "Your version is ahead! " +
                            "Are you using an experimental build?");
                }
            }

        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to check for latest version. " +
                    "Is the repository public and the latest release correctly named?");
        }
    }

    public void onDisable() {

    }

    public NamespacedKey getNBT_key() {
        return this.NBT_key;
    }

    private void createCommands() {
        this.commands = new HashMap<>();
        this.commands.put(mtReload.COMMAND, new mtReload(this));
        this.commands.put(mtBlueprints.COMMAND, new mtBlueprints(this));
        this.commands.put(mtToolNew.COMMAND, new mtToolNew(this));
        this.commands.put(mtTools.COMMAND, new mtTools(this));
        this.commands.put(mtRestoreTool.COMMAND, new mtRestoreTool(this));
        this.commands.put(mtUpgradeTool.COMMAND, new mtUpgradeTool(this));
        this.commands.put(mtDeleteTool.COMMAND, new mtDeleteTool(this));
        this.commands.put(mtTransferTool.COMMAND, new mtTransferTool(this));
        this.commands.put(mtRename.COMMAND, new mtRename(this));
        this.commands.put(mtConfirm.COMMAND, new mtConfirm(this));
        this.commands.put(mtVersion.COMMAND, new mtVersion(this));
        this.commands.put(mtUpdateDB.COMMAND, new mtUpdateDB(this));
        this.commands.put(mtUpdateInventory.COMMAND, new mtUpdateInventory(this));

        this.generalCommand = new MendingToolsCMD(this);
    }

    private void registerCommands() {
        if (this.commands == null)
            return;
//        for (Map.Entry<String, CommandExecutor> entry : this.commands.entrySet()) {
//            Objects.requireNonNull(getCommand(entry.getKey())).setExecutor(entry.getValue());
//        }
        getCommand(MendingToolsCMD.COMMAND).setExecutor(this.generalCommand);
//        getCommand(MendingToolsCMD.COMMAND).setTabCompleter(this.generalCommand);
    }

    private void updateCommandConfig() {
        for (CommandExecutor cmd_ce : this.commands.values()) {
            mtCommands cmd = (mtCommands) cmd_ce;
            cmd.setPermission();
            cmd.setTabComplete();
        }
        this.generalCommand.setPermission();
        this.generalCommand.setTabComplete();
    }

    private void addListener() {
        getServer().getPluginManager().registerEvents(new MTListener(this), this);
    }

    private void loadCachedConfig() {
        try {
            this.cachedConfig = BaseConfig_EN.getConfig(this);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[MT ERROR] Failed to load config! Change it and reload it.");
        }
        try {
            this.blueprintConfig = new BlueprintConfig((String) cachedConfig.get(
                    BaseConfig_EN.mendingToolBlueprintFile.key()));
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "[MT ERROR] Failed to load blueprints! Change it and reload it.");
        }
        try {
            this.languageConfig = TranslateConf.getConfig(this.cachedConfig);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[MT ERROR] Failed to load language config! Change it and reload it.");
        }
        if (this.dbhandler != null)
            this.dbhandler.close();
        this.dbhandler = new DBHandler(this);
    }

    public void reloadCachedConfig() {
        loadCachedConfig();
        updateCommandConfig();
    }

    public Map<String, Object> getCachedConfig() {
        return this.cachedConfig;
    }

    public BlueprintConfig getBlueprintConfig() {
        return blueprintConfig;
    }

    public Map<String, List<TextComponent>> getLanguageConfig() {
        return languageConfig;
    }

    public DBHandler get_db() {
        return this.dbhandler;
    }

    public static MendingToolsMain getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public IOpenInv getOpenInv() {
        return this.openInv;
    }

    public PersistentMetaHandler getPersistentMetaHandler() {
        return this.persistentMetaHandler;
    }

    public VaultHook getVault() {
        return this.vaultHook;
    }
}
