package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class mtTools extends mtCommands {
    public static String COMMAND = "mt_tools";

    public mtTools(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_tools.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_tools.key(), COMMAND);
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
        Player p = (Player) commandSender;
        String uuid = p.getUniqueId().toString();
        boolean oneTool = false;
        long toolID = 0;
        if (args.length > 0 && (p.hasPermission(permission + "_team") || p.isOp())) {
            try {
                toolID = Long.parseLong(args[0]);
                oneTool = true;
            } catch (NumberFormatException e) {
            }
            if (!oneTool) {
                if (args[0].equals("?")) {
                    commandHint(commandSender);
                    return false;
                } else if (args[0].equals("all")) {
                    int bookNum = 0;
                    if (args.length > 1) {
                        try {
                            bookNum = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                        }
                    }
                    Map<Integer, MendingBlueprint> blueprintMap = MendingToolsMain.getInstance().getBlueprintConfig().getBlueprints();

                    List<MendingTool> tools = main.get_db().getToolsSorted(bookNum);
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta bookMeta = (BookMeta) book.getItemMeta();
                    List<BaseComponent[]> toolPages = new LinkedList<>();
                    List<Tuple<String, Integer>> blueprintNames = new LinkedList<>();
                    String last_blueprint_name = null;
                    int page_idx = 1;
                    for (MendingTool tool : tools) {
                        String blueprint_name = blueprintMap.get(tool.getBlueprintID()).getName();
                        if (!blueprint_name.equals(last_blueprint_name)) {
                            last_blueprint_name = blueprint_name;
                            blueprintNames.add(new Tuple<>(last_blueprint_name, page_idx));
                        }
                        page_idx++;
                        toolPages.add(tool.asPage_(true));
                    }

                    List<BaseComponent[]> pages = new LinkedList<>();
                    List<BaseComponent> contentPage = new LinkedList<>();
                    int numContentPages = (int) Math.ceil(blueprintNames.size() / 9.0);
                    TextComponent tc;
                    for (Tuple<String, Integer> content : blueprintNames) {
                        tc = new TextComponent(content.t1+"\n");
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(
                                content.t2+numContentPages)));
                        contentPage.add(tc);
                        if (contentPage.size()>=9){
                            pages.add(contentPage.toArray(new BaseComponent[]{}));
                            contentPage = new LinkedList<>();
                        }
                    }
                    if (page_idx>=45){
                        contentPage.add(formatComponents(
                                (List<TextComponent>)main.getCachedConfig().get(
                                        BaseConfig_EN.EN.languageConf_text_nextBook.key()),
                                new Tuple<>("%COMMAND%", COMMAND),
                                new Tuple<>("%ID%", String.valueOf(bookNum+1))));
                    }
                    if (contentPage.size()>0){
                        pages.add(contentPage.toArray(new BaseComponent[]{}));
                    }

                    pages.addAll(toolPages);
                    assert bookMeta != null;
                    bookMeta.spigot().setPages(pages);
                    bookMeta.setTitle("MendingTools");
                    bookMeta.setAuthor("MendingTools");
                    book.setItemMeta(bookMeta);
                    p.openBook(book);
                    return false;
                }
                String lower_case_player_name = args[0].toLowerCase();
                for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                    if (lower_case_player_name.equals(player.getName().toLowerCase())) {
                        uuid = player.getUniqueId().toString();
                        break;
                    }
                }
            }
        }
        if (oneTool) {
            MendingTool tool = main.get_db().getTool(toolID);
            if (tool == null) {
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                        new Tuple<>("%ID%", String.valueOf(toolID)));
                return false;
            }
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            assert bookMeta != null;
            bookMeta.spigot().addPage(tool.asPage_(true));
            bookMeta.setTitle("MendingTools");
            bookMeta.setAuthor("MendingTools");
            book.setItemMeta(bookMeta);
            p.openBook(book);
            return false;
        }
        List<MendingTool> tools = main.get_db().getPlayerTools(uuid);
        if (tools == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_hasNoTools.key(),
                    new Tuple<>("%PLAYER%", args[0]));
            return false;
        }
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        for (MendingTool mt : tools) {
            bookMeta.spigot().addPage(mt.asPage_(true));
        }
        if (tools.size()<1){

            bookMeta.spigot().addPage(((List<BaseComponent>)main.getCachedConfig().get(BaseConfig_EN.EN.languageConf_text_noTools.key())).toArray(new BaseComponent[]{}));
        }
        bookMeta.setTitle("MendingTools");
        bookMeta.setAuthor("MendingTools");
        book.setItemMeta(bookMeta);
        p.openBook(book);
        return false;
    }
}
