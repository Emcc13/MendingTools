package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mtReload extends mtCommands {
    public static String COMMAND = "mt_reload";

    public mtReload(MendingToolsMain main) {
        super(main);
    }

    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_reload.key();
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command command, String s, String[] strings) {
        if ((commandSender instanceof Player) && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        this.main.reloadCachedConfig();
        return false;
    }
}
