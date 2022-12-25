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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class mtTools extends mtCommands {
    public static String COMMAND = "mt_tools";
    public static List<String>[] command_complete_list = new List[]{
            new ArrayList<String>() {{
                add("id");
                add("all");
                add("book");
                add("player name");
            }},
            new ArrayList<String>() {{
                add("tool id");
                add("book number");
            }},
    };

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
        Player p = (Player) commandSender;
        String uuid = p.getUniqueId().toString();
        long toolID = 0;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        List<BaseComponent[]> pages = new LinkedList<>();
        List<MendingTool> tools;
        BaseComponent nextBookCommand = formatComponents((List<TextComponent>) main.getCachedConfig().get(
                BaseConfig_EN.EN.languageConf_text_nextBook.key()));
        int bookNum;
        if (args.length > 0) {
            switch (args[0]) {
                case "?":
                    commandHint(commandSender);
                    return false;
                case "all":
                    if (!(p.hasPermission(permission + "_team") || p.isOp())) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_noPermission.key(),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                    }
                    bookNum = 0;
                    if (args.length > 1) {
                        try {
                            bookNum = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException e) {
                        }
                    }
                    nextBookCommand.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/"+MendingToolsCMD.COMMAND + " tools all " + (bookNum + 2)
                    ));
                    pages.addAll(pagesByTools(main.get_db().getToolsSorted(bookNum), true, nextBookCommand));
                    break;
                case "id":
                    if (!(args.length > 1)) {
                        commandHint(commandSender);
                        return false;
                    }
                    try {
                        toolID = Long.parseLong(args[1]);
                    } catch (NumberFormatException e) {
                        commandHint(commandSender);
                        return false;
                    }
                    MendingTool tool = main.get_db().getTool(toolID);
                    if (tool == null) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                                new Tuple<>("%ID%", String.valueOf(toolID)),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                        return false;
                    }
                    if (!tool.getUuid().equals(uuid)) {
                        if (!(p.hasPermission(permission + "_team") || p.isOp())) {
                            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_noPermission.key(),
                                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                            return false;
                        }
                        pages.addAll(tool.asPage_Teamler());
                    } else {
                        pages.addAll(tool.asPage_Player());
                    }
                    break;
                case "book":
                    bookNum = 0;
                    if (args.length > 1) {
                        try {
                            bookNum = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException e) {
                        }
                    }
                    tools = main.get_db().getPlayerTools_limited(uuid, bookNum);
                    if ((tools == null) || (tools.size() < 1)) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_text_noTools.key(),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                        return false;
                    }
                    nextBookCommand.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/"+MendingToolsCMD.COMMAND + " tools book " + (bookNum + 2)
                    ));
                    pages.addAll(pagesByTools(tools, false, nextBookCommand));
                    break;
                default:
                    if (!(p.hasPermission(permission + "_team") || p.isOp())) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_noPermission.key(),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                        return false;
                    }
                    String lower_case_player_name = args[0].toLowerCase();
                    boolean found = false;
                    for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                        if (lower_case_player_name.equals(player.getName().toLowerCase())) {
                            uuid = player.getUniqueId().toString();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notPlayed.key(),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())),
                                new Tuple<>("%PLAYER%", args[0])
                        );
                    }
                    bookNum = 0;
                    if (args.length > 1) {
                        try {
                            bookNum = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException e) {
                        }
                    }
                    tools = main.get_db().getPlayerTools_limited(uuid, bookNum);
                    if ((tools == null) || (tools.size() < 1)) {
                        sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_hasNoTools.key(),
                                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())),
                                new Tuple<>("%PLAYER%", args[0])
                                );
                        return false;
                    }
                    nextBookCommand.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/"+MendingToolsCMD.COMMAND + " tools " + lower_case_player_name + " " + (bookNum + 2)
                    ));
                    pages.addAll(pagesByTools(tools, true, nextBookCommand));
                    break;
            }
        } else {
            tools = main.get_db().getPlayerTools_limited(uuid, 0);
            if ((tools == null) || (tools.size() < 1)) {
                sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_text_noTools.key(),
                        new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                return false;
            }
            nextBookCommand.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/"+MendingToolsCMD.COMMAND + " tools book " + 2
            ));
            pages.addAll(pagesByTools(tools, false, nextBookCommand));
        }

        bookMeta.spigot().setPages(pages);
        bookMeta.setTitle("MendingTools");
        bookMeta.setAuthor("MendingTools");
        book.setItemMeta(bookMeta);
        p.openBook(book);
        return false;
    }

    protected List<BaseComponent[]> pagesByTools(List<MendingTool> tools, boolean team, BaseComponent nextBookCommand) {
        List<BaseComponent[]> pages = new LinkedList<>();
        List<BaseComponent[]> toolPages = new LinkedList<>();
        List<Tuple<String, Integer>> blueprintNames = new LinkedList<>();
        Map<Integer, MendingBlueprint> blueprintMap = MendingToolsMain.getInstance().getBlueprintConfig().getBlueprints();
        String last_blueprint_name = null;
        int page_idx = 1;
        for (MendingTool tool : tools) {
            String blueprint_name = blueprintMap.get(tool.getBlueprintID()).getName();
            if (!blueprint_name.equals(last_blueprint_name)) {
                last_blueprint_name = blueprint_name;
                blueprintNames.add(new Tuple<>(last_blueprint_name, page_idx));
            }
            List<BaseComponent[]> pages_tool;
            if (team) {
                pages_tool = tool.asPage_Teamler();
            } else {
                pages_tool = tool.asPage_Player();
            }
            page_idx += pages_tool.size();
            toolPages.addAll(pages_tool);
        }
        List<BaseComponent> contentPage = new LinkedList<>();
        TextComponent title = formatComponents((List<TextComponent>)MendingToolsMain.getInstance().getCachedConfig()
                .get(BaseConfig_EN.EN.languageConf_text_pageOfContentTitle.key()));
        title.addExtra("\n");
        contentPage.add(title);
        int numContentPages = (int) Math.ceil(blueprintNames.size() / 9.0);
        TextComponent tc;
        for (Tuple<String, Integer> content : blueprintNames) {
            tc = new TextComponent(content.t1 + "\n");
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(
                    content.t2 + numContentPages)));
            contentPage.add(tc);
            if (contentPage.size() >= 9) {
                pages.add(contentPage.toArray(new BaseComponent[]{}));
                contentPage = new LinkedList<>();
            }
        }
        if (page_idx >= 41) {
            contentPage.add(new TextComponent("\n"));
            contentPage.add(nextBookCommand);
        }
        if (contentPage.size() > 0) {
            pages.add(contentPage.toArray(new BaseComponent[]{}));
        }
        pages.addAll(toolPages);
        return pages;
    }
}
