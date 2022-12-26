package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class mtBlueprints extends mtCommands {
    public static String COMMAND = "mt_blueprints";

    public mtBlueprints(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_blueprints.key();
    }

    @Override
    protected String getTabCompleteKey(){
        return BaseConfig_EN.TabComplete.tabComplete_blueprints.key();
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
            if (commandSender instanceof Player){
                noPermission(commandSender);
            }
            return false;
        }
        Player p = (Player) commandSender;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        this.main.getBlueprintConfig().getBlueprints().keySet().stream().sorted().forEach( bpID->{
            MendingBlueprint blueprint  = main.getBlueprintConfig().getBlueprints().get(bpID);
            bookMeta.spigot().addPage(new BaseComponent[][]{blueprint.asPage().toArray(new TextComponent[]{})});
        });
        bookMeta.setTitle("MendingBlueprints");
        bookMeta.setAuthor("MendingTools");
        book.setItemMeta(bookMeta);

        p.openBook(book);
        return false;
    }
}
