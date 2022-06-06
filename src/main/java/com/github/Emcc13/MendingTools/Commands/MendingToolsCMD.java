package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MendingToolsCMD extends mtCommands {
        public static String COMMAND = "mendingtools";

        public MendingToolsCMD(MendingToolsMain main) {
            super(main);
        }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_mendingtools.key();
    }

    protected void commandHint(CommandSender commandSender){
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_mendingtools.key(), COMMAND);
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args){
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())){
            noPermission(commandSender);
            return false;
        }
        if (args.length<1){
            main.commands.get(mtTools.COMMAND).onCommand(commandSender, cmd, cmdname, args);
            return false;
        }
        switch (args[0].toLowerCase()){
            case "blueprints":
                main.commands.get(mtBlueprints.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "delete":
                main.commands.get(mtDeleteTool.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "reload":
                main.commands.get(mtReload.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "restore":
                main.commands.get(mtRestoreTool.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "new":
                main.commands.get(mtToolNew.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "transfer":
                main.commands.get(mtTransferTool.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "upgrade":
                main.commands.get(mtUpgradeTool.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "tools":
                main.commands.get(mtTools.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                commandHint(commandSender);
                break;
        }
        return false;
    }
}
