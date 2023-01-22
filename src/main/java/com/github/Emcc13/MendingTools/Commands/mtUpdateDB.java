package com.github.Emcc13.MendingTools.Commands;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Database.DBHandler;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        DBHandler db = main.get_db();
        List<MendingTool> tools;
        if (args.length>0){
            switch (args[0]){
                case "all":
                    tools = db.getAllTools();
                    break;
                default:
                    tools = db.getAllToolsWithoutBPID();
            }
        }else
            tools = db.getAllToolsWithoutBPID();
        for (MendingTool mt : tools) {
            MendingBlueprint mb = findBlueprint(mt, netherite_equal_diamond);
            if (mb == null)
                continue;
            Map<String, Integer> enchantments = mt.getEnchantments();

            if (mt.getUuid()!=null) {
                Player player = Bukkit.getPlayer(UUID.fromString(mt.getUuid()));
                if (player == null) {
                    OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(UUID.fromString(mt.getUuid()));
                    player = main.getOpenInv().loadPlayer(op);
                }
                if (player!=null){
                    ItemStack is = getItemStack(player.getInventory(), mt.getID());
                    if (is == null)
                        is = getItemStack(player.getEnderChest(), mt.getID());
                    if (is != null){
                        for (Map.Entry<Enchantment, Integer> entry : is.getEnchantments().entrySet())
                            enchantments.put(entry.getKey().getKey().getKey(), entry.getValue());
                    }
                }
            }

            db.updateBlueprintID(mt.getID(), mb, enchantments);
        }
        return false;
    }

    private ItemStack getItemStack(Inventory iv, long id){
        ItemMeta im;
        for (ItemStack itemstack : iv.getContents()){
            if (itemstack == null)
                continue;
            im = itemstack.getItemMeta();
            if (im == null)
                continue;
            Long is_id = im.getPersistentDataContainer().get(this.main.getNBT_key(), PersistentDataType.LONG);
            if (is_id == null || id != is_id)
                continue;
            return itemstack;
        }
        return null;
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
