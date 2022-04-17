package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedList;
import java.util.List;

public class mtToolNew extends mtCommands {
    public static String COMMAND = "mt_tool_new";

    public mtToolNew(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_newMendingTool.key();
    }

    protected void commandHint(CommandSender commandSender){
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_toolNew.key(), COMMAND);
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        if (args.length < 2) {
            commandHint(commandSender);
            return false;
        }
        int blueprintID;
        try{
            blueprintID = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            commandHint(commandSender);
            return false;
        }
        MendingBlueprint blueprint = main.getBlueprintConfig().getBlueprints().get(blueprintID);
        if (blueprint == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadBlueprint.key(),
                    new Tuple<>("%ID%", String.valueOf(blueprintID)));
            return false;
        }
        Player player = main.getServer().getPlayer(args[1]);
        OfflinePlayer op = null;
        boolean offline = false;
        if (player == null) {
            for (OfflinePlayer offlinePlayer : Bukkit.getServer().getOfflinePlayers()){
                if (args[1].equals(offlinePlayer.getName())){
                    op = offlinePlayer;
                    break;
                }
            }
            if (op == null || !op.hasPlayedBefore()){
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notPlayed.key(),
                        new Tuple<>("%PLAYER%", args[1]));
                return false;
            }
            offline = true;
            player = main.getOpenInv().loadPlayer(op);
            if (player == null){
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                        new Tuple<>("%PLAYER%", op.getName()));
                return false;
            }
        }

        long tool_id = MendingToolsMain.getInstance().get_db().add_tool(blueprint, player.getUniqueId().toString());
        if (tool_id < 1) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_db.key());
            return false;
        }

        ItemStack tool = blueprint.getItemStack();

        ItemMeta im = tool.getItemMeta();
        assert im != null;
        List<String> lore = im.getLore();
        if (lore == null)
            lore = new LinkedList<>();
        lore.add(player.getName());
        im.setLore(lore);
        im.getPersistentDataContainer().set(main.getNBT_key(), PersistentDataType.LONG, tool_id);
        tool.setItemMeta(im);

        player.getInventory().addItem(tool);
        if (offline){
            player.saveData();
            main.getOpenInv().unload(op);
        }
        return false;
    }
}
