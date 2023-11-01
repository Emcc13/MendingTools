package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MendingToolsCMD extends mtCommands {
    public static String COMMAND = "mendingtools";
    public static List<String> subCommands = new ArrayList<String>() {{
        add("blueprints");
        add("delete");
        add("reload");
        add("restore");
        add("new");
        add("transfer");
        add("upgrade");
        add("tools");
        add("rename");
        add("version");
    }};

    public MendingToolsCMD(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_mendingtools.key();
    }

    @Override
    protected String getTabCompleteKey(){
        return "";
    }

    @Override
    public void setTabComplete(){}

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_mendingtools.key(), COMMAND);
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        if (args.length < 1) {
            main.commands.get(mtTools.COMMAND).onCommand(commandSender, cmd, cmdname, args);
            return false;
        }
        switch (args[0].toLowerCase()) {
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
            case "rename":
                main.commands.get(mtRename.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "confirm":
                main.commands.get(mtConfirm.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "version":
                main.commands.get(mtVersion.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "update_db":
                main.commands.get(mtUpdateDB.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "update_inventory":
                main.commands.get(mtUpdateInventory.COMMAND).onCommand(commandSender, cmd, cmdname, Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                commandHint(commandSender);
                break;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        switch (strings.length) {
            case 1:
                return subCommands;
            case 2:
                switch (strings[0]) {
                    case "blueprints":
                        return ((mtCommands) main.commands.get(mtBlueprints.COMMAND)).subCommandComplete(strings);
                    case "delete":
                        return ((mtCommands) main.commands.get(mtDeleteTool.COMMAND)).subCommandComplete(strings);
                    case "reload":
                        return ((mtCommands) main.commands.get(mtReload.COMMAND)).subCommandComplete(strings);
                    case "restore":
                        return ((mtCommands) main.commands.get(mtRestoreTool.COMMAND)).subCommandComplete(strings);
                    case "new":
                        return ((mtCommands) main.commands.get(mtToolNew.COMMAND)).subCommandComplete(strings);
                    case "transfer":
                        return ((mtCommands) main.commands.get(mtTransferTool.COMMAND)).subCommandComplete(strings);
                    case "upgrade":
                        return ((mtCommands) main.commands.get(mtUpgradeTool.COMMAND)).subCommandComplete(strings);
                    case "tools":
                        return ((mtCommands) main.commands.get(mtTools.COMMAND)).subCommandComplete(strings);
                    case "rename":
                        return ((mtCommands) main.commands.get(mtRename.COMMAND)).subCommandComplete(strings);
                    case "version":
                        return ((mtCommands) main.commands.get(mtVersion.COMMAND)).subCommandComplete(strings);
                    default:
                        return this.subCommandComplete(strings);
                }
        }
        return null;
    }

    @Override
    public List<String> subCommandComplete(String[] args) {
        return new ArrayList<String>() {{
            add("invalid subcommand");
        }};
    }
}
