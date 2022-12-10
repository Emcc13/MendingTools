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

public class mtDeleteTool extends mtCommands {
    public static String COMMAND = "mt_delete_tool";
    public static List<String> command_complete_list[] = new List[]{
            new ArrayList<String>() {{
                add("blueprint id");
            }},
    };

    public mtDeleteTool(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_deleteTool.key();
    }

    protected void commandHint(CommandSender commandSender){
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_deleteTool.key(), COMMAND);
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
        if (args.length < 1) {
            commandHint(commandSender);
            return false;
        }
        Long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            commandHint(commandSender);
            return false;
        }
        MendingTool tool = main.get_db().getTool(id);
        if (!main.get_db().delete_tool(id)) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                    new Tuple<>("%ID%", String.valueOf(id)),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }

        if (tool.isBroken())
            return false;
        Player p = Bukkit.getPlayer(UUID.fromString(tool.getUuid()));
        boolean offline = false;
        OfflinePlayer op = null;
        if (p == null) {
            op = Bukkit.getServer().getOfflinePlayer(UUID.fromString(tool.getUuid()));
            if (!op.hasPlayedBefore()) {
                sendErrorMessage(commandSender,BaseConfig_EN.EN.languageConf_error_notPlayed.key(),
                        new Tuple<>("%PLAYER%", op.getName()),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                        new Tuple<>("%ID%", String.valueOf(tool.getID())),
                        new Tuple<>("%PLAYER%", op.getName()),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                return false;
            }
            offline = true;
            p = main.getOpenInv().loadPlayer(op);
            if (p == null) {
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                        new Tuple<>("%PLAYER%", op.getName()),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                        new Tuple<>("%ID%", String.valueOf(tool.getID())),
                        new Tuple<>("%PLAYER%", op.getName()),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                return false;
            }
        }

        boolean removedItem = false;
        ItemStack[] contents = p.getInventory().getContents();
        ItemStack tmpStack;
        for (int idx=0; idx<contents.length; idx++){
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
            contents[idx]=null;
            removedItem = true;
        }
        p.getInventory().setContents(contents);
        p.updateInventory();

        for (ItemStack itemStack : p.getEnderChest()) {
            if (itemStack == null) {
                continue;
            }
            ItemMeta im = itemStack.getItemMeta();
            assert im != null;
            Long is_id = im.getPersistentDataContainer().get(this.main.getNBT_key(), PersistentDataType.LONG);
            if (!id.equals(is_id)) {
                continue;
            }
            p.getEnderChest().remove(itemStack);
            removedItem = true;
        }
        if (offline) {
            p.saveData();
            main.getOpenInv().unload(op);
        }
        if (!removedItem){
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_removingItem.key(),
                    new Tuple<>("%ID%", String.valueOf(tool.getID())),
                    new Tuple<>("%PLAYER%", op.getName()),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
        }
        return false;
    }
}
