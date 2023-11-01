package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.InventoryUtil;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mtUpdateInventory extends mtCommands {
    public static String COMMAND = "mt_update_inventory";

    public mtUpdateInventory(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return "mt.updateinventory";
    }

    public void setPermission() {
        this.permission = "mt.updateinventory";
    }

    @Override
    protected String getTabCompleteKey() {
        return null;
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_transferTool.key(), COMMAND);
    }

    public void setTabComplete() {
        this.command_complete_list = null;
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        if (args.length<1){
            commandHint(commandSender);
            return false;
        }
        switch (args[0]){
            case "?":
                commandHint(commandSender);
                return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notOnline.key(),
                    new Tuple<>("%PLAYER%", args[1]),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        InventoryUtil.scanPlayerInventory(player);
        return false;
    }
}
