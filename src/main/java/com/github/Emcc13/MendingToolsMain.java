package com.github.Emcc13;

import com.github.Emcc13.MendingTools.Commands.*;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Config.BlueprintConfig;
import com.github.Emcc13.MendingTools.Database.DBHandler;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.lishid.openinv.IOpenInv;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MendingToolsMain extends JavaPlugin {
    private static MendingToolsMain instance;
    private Map<String, Object> cachedConfig;
    private BlueprintConfig blueprintConfig;
    private DBHandler dbhandler;
    private NamespacedKey NBT_key;
    private Economy economy;
    private IOpenInv openInv;
    private mtCommands generalCommand;

    public Map<String, CommandExecutor> commands;

    public MendingToolsMain() {
        instance = this;
    }

    public void onEnable() {
        this.cachedConfig = BaseConfig_EN.getConfig(this);
        this.blueprintConfig = new BlueprintConfig((String) cachedConfig.get(
                BaseConfig_EN.mendingToolBlueprintFile.key()));
        this.dbhandler = new DBHandler(this);
        this.NBT_key = new NamespacedKey(this, "ID");
        this.economy = Objects.requireNonNull(this.getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
        this.openInv = (IOpenInv) this.getServer().getPluginManager().getPlugin("OpenInv");
        this.createCommands();
        this.registerCommands();
        this.setCommandPermissions();
        this.addListener();
    }

    public void onDisable() {

    }

    public NamespacedKey getNBT_key(){
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

    private void setCommandPermissions() {
        for (CommandExecutor cmd_ce : this.commands.values()) {
            mtCommands cmd = (mtCommands) cmd_ce;
            cmd.setPermission();
        }
        this.generalCommand.setPermission();
    }

    private void addListener(){
        getServer().getPluginManager().registerEvents(new MTListener(this), this);
    }

    public void reloadCachedConfig() {
        this.cachedConfig = BaseConfig_EN.getConfig(this);
        this.blueprintConfig = new BlueprintConfig((String) cachedConfig.get(
                BaseConfig_EN.mendingToolBlueprintFile.key()));
        this.dbhandler = new DBHandler(this);
        setCommandPermissions();
    }

    public Map<String, Object> getCachedConfig() {
        return this.cachedConfig;
    }

    public BlueprintConfig getBlueprintConfig() {
        return blueprintConfig;
    }

    public DBHandler get_db(){
        return this.dbhandler;
    }

    public static MendingToolsMain getInstance() {
        return instance;
    }

    public Economy getEconomy(){
        return this.economy;
    }

    public IOpenInv getOpenInv(){
        return this.openInv;
    }

}
