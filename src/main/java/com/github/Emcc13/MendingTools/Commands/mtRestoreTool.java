package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.github.Emcc13.MendingTools.Util.Equationparser;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

public class mtRestoreTool extends mtCommands {
    public static String COMMAND = "mt_restore_tool";

    public mtRestoreTool(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_restoreTool.key();
    }

    @Override
    protected String getTabCompleteKey() {
        return BaseConfig_EN.TabComplete.tabComplete_restoreTool.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_restoreTool.key(), COMMAND);
    }

    public List<String> subCommandComplete(String[] args) {
        if (this.command_complete_list != null && args.length - 1 <= this.command_complete_list.length) {
            return this.command_complete_list[args.length - 2];
        }
        return null;
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (!(commandSender instanceof Player) || !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        Player p_executor = (Player) commandSender;
        Player p_receiver = p_executor;
        OfflinePlayer op_receiver = null;
        boolean offline = false;
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            commandHint(commandSender);
            return false;
        }
        MendingTool tool = main.get_db().getTool(id);
        if (tool == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                    new Tuple<>("%ID%", String.valueOf(id)),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        if (!tool.isBroken()) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_hint_toolNotBroken.key(),
                    new Tuple<>("%ID%", String.valueOf(tool.getID())),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        MendingBlueprint blueprint = main.getBlueprintConfig().getBlueprints().get(tool.getBlueprintID());
        if (blueprint == null && !(p_executor.hasPermission("mt.admin") || p_executor.isOp())) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadBlueprint.key(),
                    new Tuple<>("%ID%", String.valueOf(tool.getBlueprintID())),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        if (!p_executor.getUniqueId().toString().equals(tool.getUuid())) {
            if (!(p_executor.hasPermission(permission + "_team") || p_executor.isOp()))
                return false;
            p_receiver = Bukkit.getServer().getPlayer(UUID.fromString(tool.getUuid()));
            if (p_receiver == null) {
                op_receiver = Bukkit.getServer().getOfflinePlayer(UUID.fromString(tool.getUuid()));
                offline = true;
                p_receiver = main.getOpenInv().loadPlayer(op_receiver);
                if (p_receiver == null) {
                    sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                            new Tuple<>("%PLAYER%", tool.getUuid()),
                            new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                    return false;
                }
            }
        }
        if (!MTListener.has_free_slot(p_receiver)){
            sendErrorMessage(p_executor, BaseConfig_EN.EN.languageConf_error_noSpaceInInventory.key(),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()))
                    );
            return false;
        }

        ItemStack is = tool.getItemStack(p_receiver.getName());
        double toPay = 0;
        if (blueprint != null && blueprint.getMoney() != null) {
            toPay = Equationparser.eval(blueprint.getMoney(), new HashMap<String, Double>() {{
                put("%RESTORES%", (double) tool.getRestores());
                put("%#ENCH%", (double) tool.getEnchantments().size());
                for (Map.Entry<String, Integer> entry : tool.getEnchantments().entrySet())
                    put("%" + entry.getKey() + "%", entry.getValue()==null?0.0:entry.getValue().doubleValue());
            }});
            if (main.getEconomy().getBalance(p_receiver) < toPay) {
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notEnoughMoney.key(),
                        new Tuple<>("%PLAYER%", p_receiver.getName()),
                        new Tuple<>("%MONEY%", String.valueOf(toPay)),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()))
                );
                return false;
            }
        }
        if (!main.get_db().restore_tool(id)) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_db.key(),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        if (blueprint != null && blueprint.getMoney() != null) {
            main.getEconomy().withdrawPlayer(p_receiver, toPay);
        }
        if (blueprint != null && blueprint.getRestore_commands() != null) {
            List<String> commands = new LinkedList<>();
            for (String command : blueprint.getRestore_commands()) {
                commands.add(formatCommand(command, p_receiver, tool, toPay,
                        new HashMap<String, String>() {{
                            for (Map.Entry<String, Integer> entry : tool.getEnchantments().entrySet())
                                put(entry.getKey(), (entry.getValue()==null?"1":entry.getValue().toString()));
                            put("%BPNAME%", blueprint.getName());
                            put("%BPID%", String.valueOf(blueprint.getID()));
                            put("%RESTORES%", String.valueOf(tool.getRestores()));
                        }}));
            }
            Bukkit.getScheduler().runTaskLater(main, () -> {
                String latestCommand = "";
                try {
                    for (String command : commands) {
                        latestCommand = command;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Caught exception running: " + latestCommand);
                    e.printStackTrace();
                }
            }, 0);
        }
        p_receiver.getInventory().addItem(is);
        if (offline) {
            p_receiver.saveData();
            main.getOpenInv().unload(op_receiver);
        }
        return false;
    }

    private String formatCommand(String command, Player p, MendingTool tool, double moneyValue, Map<String, String> others) {
        command = command.replace("%PLAYER%", p.getName());
        command = command.replace("%ID%", String.valueOf(tool.getID()));
        command = command.replace("%MONEY%", String.format("%,.0f", moneyValue));
        for (Map.Entry<String, String> entry : others.entrySet()) {
            command = command.replace(entry.getKey(), entry.getValue());
        }
        return command;
    }
}
