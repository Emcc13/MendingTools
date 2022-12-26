package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class mtVersion extends mtCommands {
    public static String COMMAND = "mt_version";

    public mtVersion(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_version.key();
    }

    @Override
    protected String getTabCompleteKey() {
        return BaseConfig_EN.TabComplete.tabComplete_version.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_version.key(), COMMAND);
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
        Map<String, Object> cached_config = main.getCachedConfig();
        p.spigot().sendMessage(formatComponents(
                (List<TextComponent>) cached_config.get(BaseConfig_EN.EN.languageConf_text_version.key()),
                new Tuple<String, String>("%PREFIX%", (String) cached_config.get(BaseConfig_EN.languageConf_prefix.key())),
                new Tuple<String, String>("%VERSION%", main.getDescription().getVersion())
        ));
        return false;
    }
}
