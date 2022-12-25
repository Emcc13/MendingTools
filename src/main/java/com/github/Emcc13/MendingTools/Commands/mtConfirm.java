package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class mtConfirm extends mtCommands {
    public static String COMMAND = "mt_confirm";
    public static List<String> command_complete_list[] = new List[]{
            new ArrayList<String>() {{
                add("text string");
            }},
    };


    public mtConfirm(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_confirm.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_confirm.key(), COMMAND);
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
        if (args.length < 3) {
            commandHint(commandSender);
            return false;
        }
        Player p = (Player) commandSender;
        List<TextComponent> message = (List<TextComponent>) this.main.getCachedConfig().get(args[0]);
        Map<String, String> replacements = new HashMap<String, String>();
        String[] values;
        for (int i = 1; i < args.length; i++) {
            values = args[i].split("=");
            replacements.put(values[0].replace("$", "%"), values[1]);
        }
        replacements.put("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()));
        try {
            long toolID = Integer.parseInt(replacements.get("%ID%"));
            MendingTool tool = MendingToolsMain.getInstance().get_db().getTool(toolID);
            p = Bukkit.getServer().getPlayer(UUID.fromString(tool.getUuid()));
            if (p == null){
                p = (Player) commandSender;
            }
        }catch (Exception ignored){
        }
        p.spigot().sendMessage(formatComponents(message, replacements));
        return false;
    }

    protected TextComponent formatComponents(List<TextComponent> template, Map<String, String> replacements) {
        TextComponent result = new TextComponent();
        for (TextComponent tc : template) {
            TextComponent copy = new TextComponent(formatString(tc.getText(), replacements));
            HoverEvent hover = tc.getHoverEvent();
            if (hover != null) {
                List<Content> contents = new LinkedList<>();
                for (Content content : hover.getContents()) {
                    contents.add(new Text(formatString(
                            ((String) ((Text) content).getValue()),
                            replacements
                    )));
                }
                copy.setHoverEvent(new HoverEvent(hover.getAction(), contents));
            }
            ClickEvent click = tc.getClickEvent();
            if (click != null) {
                copy.setClickEvent(new ClickEvent(click.getAction(),
                        formatString(click.getValue(), replacements)));
            }
            result.addExtra(copy);
        }
        return result;
    }

    private static String formatString(String template, Map<String, String> replacements) {
        String result = template;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }
}
