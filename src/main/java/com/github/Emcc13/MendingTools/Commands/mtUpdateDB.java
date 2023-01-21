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
        return "mt.updatedb";
    }

    public void setPermission() {
        this.permission = "mt.updatedb";
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
            db.updateBlueprintID(mt.getID(), mb, mt.getEnchantments());
        }
        return false;
    }

    private MendingBlueprint findBlueprint(MendingTool mendingTool, boolean netherite_equal_diamond) {
        Map<String, Integer> isEnch = mendingTool.getEnchantments();
        MendingBlueprint last_bp=null;
        Integer last_dist=null;
        Integer ench_lvl;
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
            boolean isOk = true;
            for (MendingBlueprint.MTEnchantment ench : mb.getEnchantments()) {
                ench_lvl = isEnch.get(ench.getEnchantment().getKey().getKey());
                if (ench_lvl!=null &&
                        ench.getMaxlevel()>=ench_lvl &&
                        ench.getLevel()<= ench_lvl
                )
                    continue;
                isOk = false;
            }
            if (isOk){
                int dist = isEnch.size()-mb.getEnchantments().size();
                if (last_dist == null || dist < last_dist){
                    last_dist = dist;
                    last_bp = mb;
                }
            }
        }
        return last_bp;
    }
}
