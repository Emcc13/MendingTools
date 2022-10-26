package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class mtRename extends mtCommands {
    public static String COMMAND = "mt_rename";
    public static List<String> command_complete_list[] = new List[]{
            new ArrayList<String>() {{
                add("new name");
            }},
    };

    public mtRename(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_renameTool.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_renameTool.key(), COMMAND);
    }

    public List<String> subCommandComplete(String[] args){
        if (this.command_complete_list != null && args.length-1<=this.command_complete_list.length) {
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
        if (args.length < 1) {
            commandHint(commandSender);
            return false;
        }
//        String new_name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String new_name = String.join(" ", args);
        Player p = (Player) commandSender;
        ItemStack is = p.getInventory().getItemInMainHand();
        if (is.hasItemMeta() &&
                is.getItemMeta().getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(new_name);
            is.setItemMeta(im);
        }
//        p.getInventory().setItemInMainHand(is);
        return false;
    }
}
