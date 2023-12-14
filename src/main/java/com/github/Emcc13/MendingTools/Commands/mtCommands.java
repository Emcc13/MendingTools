package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class mtCommands implements TabExecutor {
    protected MendingToolsMain main;
    protected String permission;
    public List<String> command_complete_list[];
    public static String COMMAND;

    public mtCommands(MendingToolsMain main) {
        this.main = main;
        this.setPermission();
        this.setTabComplete();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                runCommandLater(commandSender, cmd, cmdname, args);
            }
        });
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    public List<String> subCommandComplete(String[] args) {
        if (this.command_complete_list != null && args.length - 1 <= this.command_complete_list.length) {
            return this.command_complete_list[args.length - 2];
        }
        return null;
    }

    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        return false;
    }

    public void setPermission() {
        this.permission = (String) main.getCachedConfig().get(this.getPerm_key());
    }

    public void setTabComplete() {
        this.command_complete_list = ((List<List<String>>) main.getCachedConfig().
                get(this.getTabCompleteKey())).toArray(new ArrayList[]{});
    }

    protected abstract String getPerm_key();

    protected abstract String getTabCompleteKey();

    protected TextComponent formatComponents(List<TextComponent> template, Tuple<String, String>... replacements) {
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

    protected void commandHint(CommandSender commandSender, String key, String COMMAND) {
        TextComponent message = formatComponents((List<TextComponent>) main.getCachedConfig().get(key),
                new Tuple<>("%COMMAND%", COMMAND)
        );
        if (commandSender instanceof Player)
            ((Player) commandSender).spigot().sendMessage(message);
        else
            commandSender.sendMessage(message.toString());
    }

    protected void noPermission(CommandSender commandSender) {
        if (commandSender instanceof Player)
            ((Player) commandSender).spigot().sendMessage(formatComponents(
                    (List<TextComponent>) main.getCachedConfig().
                            get(BaseConfig_EN.EN.languageConf_noPermission.key()),
                    new Tuple<>("%PREFIX%", (String) main.getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()))));
    }

    protected void sendErrorMessage(CommandSender commandSender, String key, Tuple<String, String>... replacements) {
        TextComponent errorMesage = formatComponents((List<TextComponent>) main.getCachedConfig().get(key), replacements);
        if (commandSender instanceof Player) {
            ((Player) commandSender).spigot().sendMessage(errorMesage);
        } else {
            commandSender.sendMessage(errorMesage.toString());
        }
    }

    protected static String formatString(String template, Tuple<String, String>... replacements) {
        String result = template;
        for (Tuple<String, String> replacement : replacements) {
            result = result.replace(replacement.t1, replacement.t2);
        }
        return result;
    }
}
