package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Config.TranslateConf;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.github.Emcc13.MendingTools.Util.Equationparser;
import com.github.Emcc13.MendingTools.Util.Tuple;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class mtUpgradeTool extends mtCommands {
    public static String COMMAND = "mt_upgrade_tool";

    public mtUpgradeTool(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return BaseConfig_EN.perm_command_upgradeTool.key();
    }

    @Override
    protected String getTabCompleteKey() {
        return BaseConfig_EN.TabComplete.tabComplete_upgradeTool.key();
    }

    protected void commandHint(CommandSender commandSender) {
        super.commandHint(commandSender, BaseConfig_EN.EN.languageConf_hint_upgradeTool.key(), COMMAND);
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
        Long id;
        try {
            id = Long.valueOf(args[0]);
        } catch (NumberFormatException e) {
            commandHint(commandSender);
            return false;
        }
        MendingTool tool = main.get_db().getTool(id);
        if (tool == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchTool.key(),
                    new Tuple<>("%ID%", String.valueOf(id)),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        String enchantment = args[1].toLowerCase();
//        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment));
        List<TextComponent> lang_ench = main.getLanguageConfig().getOrDefault(
                TranslateConf.languageConf_enchantment + enchantment,
                new LinkedList<TextComponent>() {{
                    add(new TextComponent(enchantment.substring(2)));
                }}
        );
        String lang_ench_str = formatComponents(lang_ench).toPlainText();
//        if (ench == null) {
//            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_noSuchEnchantment.key(),
//                    new Tuple<>("%ENCH%", args[1]),
//                    new Tuple<>("%DISP-ENCH%", lang_ench_str),
//                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
//            return false;
//        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            commandHint(commandSender);
            return false;
        }
        Integer curr_level = tool.getEnchantmentLevel(enchantment);
        if (curr_level == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_toolHasNotEnchantment.key(),
                    new Tuple<>("%ID%", String.valueOf(String.valueOf(id))),
                    new Tuple<>("%DISP-ENCH%", lang_ench_str),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()))
            );
            return false;
        }
        if (level <= curr_level) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_targetLevelBelow.key(),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key()))
            );
            return false;
        }

        MendingBlueprint blueprint = main.getBlueprintConfig().getBlueprints().get(tool.getBlueprintID());
        if (blueprint == null) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadBlueprint.key(),
                    new Tuple<>("%ID%", String.valueOf(tool.getBlueprintID())),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        Player p_withInv;

        OfflinePlayer op = null;
        boolean offline = false;
        if (p.getUniqueId().toString().equals(tool.getUuid())) {
            p_withInv = p;
        } else {
            if (!(p.hasPermission(permission + "_team") || p.isOp())) {
                noPermission(commandSender);
                return false;
            }
            Player p_other = Bukkit.getPlayer(UUID.fromString(tool.getUuid()));
            if (p_other == null) {
                op = Bukkit.getServer().getOfflinePlayer(UUID.fromString(tool.getUuid()));
                offline = true;
                p_other = main.getOpenInv().loadPlayer(op);
                if (p_other == null) {
                    sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_loadOfflinePlayer.key(),
                            new Tuple<>("%PLAYER%", p_other.getName()),
                            new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
                    return false;
                }
            }
            p_withInv = p_other;
        }

        int currentLevel = tool.getEnchantmentLevel(enchantment);
        int runningLevel = currentLevel;

        MendingBlueprint.MTEnchantment blueprintEnch = blueprint.getEnchantment(enchantment);
        double moneyValue = 0;
        for (int intermediateLevel = currentLevel + 1; intermediateLevel <= level; intermediateLevel++) {
            if (!blueprint.upgradeAllowed(enchantment, intermediateLevel)) {
                break;
            }
            Double dLevel = (double) intermediateLevel;
            moneyValue = Equationparser.eval(blueprintEnch.getMoney(), new HashMap<String, Double>() {{
                put("%LEVEL%", dLevel);
                for (Map.Entry<String, Integer> entry : tool.getEnchantments().entrySet()) {
                    put("%" + entry.getKey() + "%", (double) entry.getValue());
                }
            }});
            if (main.getEconomy().getBalance(p_withInv) < moneyValue) {
                break;
            }
            main.getEconomy().withdrawPlayer(p_withInv, moneyValue);
            List<String> formattedCommands = new LinkedList<>();
            if (blueprintEnch.getCommands() != null) {
                for (String command : blueprintEnch.getCommands()) {
                    formattedCommands.add(formatCommand(command, p_withInv, tool, intermediateLevel, moneyValue,
                            new HashMap<String, String>() {{
                                for (Map.Entry<String, Integer> entry : tool.getEnchantments().entrySet())
                                    put("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));
                                put("%BPNAME%", blueprint.getName());
                                put("%BPID%", String.valueOf(blueprint.getID()));
                                put("%RESTORES%", String.valueOf(tool.getRestores()));
                            }}));
                }
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    for (String command : formattedCommands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }, 0);
            }
            runningLevel = intermediateLevel;
        }
        final int finalLevel = runningLevel;

        Consumer<ItemStack> toolUpgrader = itemStack -> {
            if (itemStack == null)
                return;
            ItemMeta im = itemStack.getItemMeta();
            if (im == null)
                return;
            Long is_id = im.getPersistentDataContainer().get(this.main.getNBT_key(), PersistentDataType.LONG);
            if (!id.equals(is_id)) {
                return;
            }
            try {
                itemStack.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantment)), finalLevel);
            } catch (IllegalArgumentException e) {
                List<String> lore = new LinkedList<>();
                for (String line : im.getLore()){
                    if (line.length() > 0 && line.substring(1).startsWith(enchantment.substring(1))){
                        lore.add(ChatColor.translateAlternateColorCodes('&', enchantment + " " + MTListener.int2roman(finalLevel)));
                    }else{
                        lore.add(line);
                    }
                }
                im.setLore(lore);
                itemStack.setItemMeta(im);
            }
        };
        if (finalLevel < level) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_notEnoughMoney.key(),
                    new Tuple<>("%PLAYER%", p_withInv.getName()),
                    new Tuple<>("%MONEY%", String.format("%,.0f", moneyValue)),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }

        if (!main.get_db().upgradeToolEnchantment(id, enchantment, finalLevel)) {
            sendErrorMessage(commandSender, BaseConfig_EN.EN.languageConf_error_db.key(),
                    new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())));
            return false;
        }
        if (!tool.isBroken()) {
            p_withInv.getEnderChest().forEach(toolUpgrader);
            p_withInv.getInventory().forEach(toolUpgrader);
            if (offline) {
                p_withInv.saveData();
                main.getOpenInv().unload(op);
            }
        }
        TextComponent message = formatComponents((List<TextComponent>) main.getCachedConfig().
                        get(BaseConfig_EN.EN.languageConf_text_upgradedTool.key()),
                new Tuple<>("%PREFIX%", (String) MendingToolsMain.getInstance().getCachedConfig().get(BaseConfig_EN.languageConf_prefix.key())),
                new Tuple<>("%ID%", String.valueOf(String.valueOf(id))),
                new Tuple<>("%DISP-ENCH%", lang_ench_str),
                new Tuple<>("%MONEY%", String.format("%,.0f", moneyValue)),
                new Tuple<>("%BPNAME%", blueprint.getName()),
                new Tuple<>("%LEVEL%", String.valueOf(finalLevel))
        );
        p_withInv.spigot().sendMessage(message);
        return false;
    }

    private String formatCommand(String command, Player p, MendingTool tool, int level, double moneyValue, Map<String, String> others) {
        command = command.replace("%PLAYER%", p.getName());
        command = command.replace("%LEVEL%", String.valueOf(level));
        command = command.replace("%ID%", String.valueOf(tool.getID()));
        command = command.replace("%MONEY%", String.format("%,.0f", moneyValue));
        for (Map.Entry<String, String> entry : others.entrySet()) {
            command = command.replace(entry.getKey(), entry.getValue());
        }
        return command;
    }
}
