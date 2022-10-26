package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class mtTransferTool extends mtCommands{
    public static String COMMAND = "mt_transfer";
    public static List<String> command_complete_list[] = new List[]{
            new ArrayList<String>() {{
                add("blueprint id");
            }},
            new ArrayList<String>() {{
                add("player name");
            }},
    };

    public mtTransferTool(MendingToolsMain main){
        super(main);
    }

    @Override
    protected String getPerm_key(){
        return BaseConfig_EN.perm_command_transferTool.key();
    }

    protected void commandHint(CommandSender commandSender){
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_transferTool.key(), COMMAND);
    }

    public List<String> subCommandComplete(String[] args){
        if (this.command_complete_list != null && args.length-1<=this.command_complete_list.length) {
            return this.command_complete_list[args.length - 2];
        }
        return null;
    }

    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        if (args.length<2){
            commandHint(commandSender);
            return false;
        }
        Long id;
        try {
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            commandHint(commandSender);
            return false;
        }
        MendingTool tool = main.get_db().getTool(id);
        if (tool==null){
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                    new Tuple<>("%ID%", String.valueOf(id)));
            return false;
        }

        Player toPlayer = main.getServer().getPlayer(args[1]);
        OfflinePlayer toOP = null;
        boolean toOffline = false;
        if (toPlayer==null){
            for (OfflinePlayer offlinePlayer : main.getServer().getOfflinePlayers()){
                if (args[1].equals(offlinePlayer.getName())){
                    toOP = offlinePlayer;
                    break;
                }
            }
            if (toOP == null || !toOP.hasPlayedBefore()){
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notPlayed.key(),
                        new Tuple<>("%PLAYER%", toOP.getName()));
                return false;
            }
            toOffline = true;
            toPlayer = main.getOpenInv().loadPlayer(toOP);
            if (toPlayer == null){
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                        new Tuple<>("%PLAYER%", toPlayer.getName()));
                return false;
            }
        }

        if (!main.get_db().transfer_tool(id, toPlayer.getUniqueId().toString())){
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_db.key());
            return false;
        }

        if (tool.isBroken())
            return false;

        Player fromPlayer = Bukkit.getPlayer(UUID.fromString(tool.getUuid()));
        OfflinePlayer fromOP = null;
        boolean fromOffline = false;
        if (fromPlayer == null) {
            fromOP = Bukkit.getServer().getOfflinePlayer(UUID.fromString(tool.getUuid()));
            if (!fromOP.hasPlayedBefore()){
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notPlayed.key(),
                        new Tuple<>("%PLAYER%", fromOP.getName()));
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                        new Tuple<>("%ID%", String.valueOf(tool.getID())),
                        new Tuple<>("%PLAYER%", fromPlayer.getName()));
                return false;
            }
            fromOffline = true;
            fromPlayer = main.getOpenInv().loadPlayer(fromOP);
            if (fromPlayer == null) {
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                        new Tuple<>("%PLAYER%", fromPlayer.getName()));
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                        new Tuple<>("%ID%", String.valueOf(tool.getID())),
                        new Tuple<>("%PLAYER%", fromPlayer.getName()));
                return false;
            }
        }

        boolean removedItem = false;
        ItemStack[] contents = fromPlayer.getInventory().getContents();
        ItemStack tmpStack;
        for (int idx=0; idx< contents.length; idx++){
            tmpStack = contents[idx];
            if (tmpStack == null) {
                continue;
            }
            ItemMeta im = tmpStack.getItemMeta();
            assert im != null;
            Long is_id = im.getPersistentDataContainer().get(this.main.getNBT_key(), PersistentDataType.LONG);
            if (!id.equals(is_id)) {
                continue;
            }

            contents[idx] = null;
            removedItem = true;
            toPlayer.getInventory().addItem(tmpStack);
        }
        fromPlayer.getInventory().setContents(contents);
        fromPlayer.updateInventory();
        toPlayer.updateInventory();

        for (ItemStack itemStack : fromPlayer.getEnderChest()) {
            if (itemStack == null) {
                continue;
            }
            ItemMeta im = itemStack.getItemMeta();
            assert im != null;
            Long is_id = im.getPersistentDataContainer().get(this.main.getNBT_key(), PersistentDataType.LONG);
            if (!id.equals(is_id)) {
                continue;
            }
            fromPlayer.getEnderChest().remove(itemStack);
            removedItem = true;
            toPlayer.getInventory().addItem(itemStack);
        }
        if (fromOffline){
            fromPlayer.saveData();
            main.getOpenInv().unload(fromOP);
        }
        if (toOffline){
            toPlayer.saveData();
            main.getOpenInv().unload(toOP);
        }
        if (!removedItem){
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                    new Tuple<>("%ID%", String.valueOf(tool.getID())),
                    new Tuple<>("%PLAYER%", fromPlayer.getName()));
        }
        return false;
    }
}
