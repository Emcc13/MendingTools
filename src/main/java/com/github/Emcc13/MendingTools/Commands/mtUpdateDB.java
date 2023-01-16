package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Database.DBHandler;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class mtUpdateDB extends mtCommands {
    public static String COMMAND = "mt_update_db";

    public mtUpdateDB(MendingToolsMain main) {
        super(main);
    }

    @Override
    protected String getPerm_key() {
        return "mt.reload";
    }

    public void setPermission() {
        this.permission = "mt.reload";
    }

    @Override
    protected String getTabCompleteKey() {
        return null;
    }

    public void setTabComplete() {
        this.command_complete_list = null;
    }


    @Override
    protected boolean runCommandLater(CommandSender commandSender, Command cmd, String cmdname, String[] args) {
        if (commandSender instanceof Player && !(commandSender.hasPermission(permission) || commandSender.isOp())) {
            noPermission(commandSender);
            return false;
        }
        boolean netherite_equal_diamond = (boolean) main.getCachedConfig().get(BaseConfig_EN.option_netherite_equal_diamond.key());
        System.out.println(netherite_equal_diamond);
        DBHandler db = main.get_db();
        for (MendingTool mt : db.getAllToolsWithoutBPID()) {
            MendingBlueprint mb = findBlueprint(mt, netherite_equal_diamond);
            if (mb == null)
                continue;
            System.out.println("Updated:");
            System.out.println(db.updateBlueprintID(mt.getID(), mb.getID(), mb.getMaterial()));
        }
        return false;
    }

    private MendingBlueprint findBlueprint(MendingTool mendingTool, boolean netherite_equal_diamond) {
        Map<String, Integer> isEnch = mendingTool.getEnchantments();
        for (MendingBlueprint mb : main.getBlueprintConfig().getBlueprints().values()) {
            String[] mb_mat = mb.getMaterial().split("_");
            String[] is_mat = mendingTool.getMaterial().split("_");
            if (!(mb.getMaterial().equals(mendingTool.getMaterial())) &&
                    !(netherite_equal_diamond &&
                            mb_mat[0].toLowerCase().equals("diamond") &&
                            is_mat[0].toLowerCase().equals("netherite") &&
                            mb_mat[1].toLowerCase().equals(is_mat[1].toLowerCase())
                    ))
                continue;
            if (mb.getEnchantments().size() != mendingTool.getEnchantments().size()) {
                continue;
            }
            boolean isOk = true;
            for (MendingBlueprint.MTEnchantment ench : mb.getEnchantments()) {
                if (ench.getMaxlevel() >= isEnch.getOrDefault(ench.getEnchantment(), 0))
                    continue;
                isOk = false;
            }
            if (isOk)
                for (Map.Entry<String, Integer> ench_entry : mendingTool.getEnchantments().entrySet()) {
                    if (mb.getEnchantment(ench_entry.getKey()) != null &&
                            ench_entry.getValue() <= mb.getEnchantment(ench_entry.getKey()).getMaxlevel())
                        continue;
                    isOk = false;
                }
            if (isOk)
                return mb;
        }
        return null;
    }
}
