package com.github.Emcc13.MendingTools.Util;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class InventoryUtil {
    public static void scanPlayerInventory(Player p){
        MendingToolsMain main = MendingToolsMain.getInstance();
        Enchantment ench = Enchantment.MENDING;
        Map<Long, MendingTool> tools = new HashMap<Long, MendingTool>();
        List<MendingTool> tmp = main.get_db().getPlayerTools(p.getUniqueId().toString());
        if (tmp == null) {
            tmp = new LinkedList<>();
        }
        for (MendingTool tool : tmp) {
            tools.put(tool.getID(), tool);
        }

        Consumer<ItemStack> item_register = itemStack -> {
            if (itemStack == null)
                return;
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            if (!meta.hasEnchant(ench))
                return;
            Long id = meta.getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new LinkedList<>();
            }

            MendingBlueprint mb;
            if (id != null){
                MendingTool mt = main.get_db().getTool(id);
                if (mt==null){
                    main.getLogger().log(Level.WARNING, "Unregistered Tool with ID: "+id+" detected! Is the database corrupted?");
                    id = null;
                }else {
                    if (tools.containsKey(id))
                        tools.remove(id);
                    else
                        main.getLogger().log(Level.SEVERE, "Duplicated tool detected! ID: " + id);

                    Map<String, Integer> new_lore_enchantments = new HashMap<>();
                    Integer ench_level;
                    String ench_text;
                    for (String lore_line : lore){
                        if (lore_line.startsWith("&7")){
                            String[]lore_ench = lore_line.split(" ");
                            ench_text = lore_ench[0];
                            try {
                                ench_level = lore_ench.length>1?MendingBlueprint.roman2int(lore_ench[1]):1;
                            }catch (Exception e){
                                ench_level = 0;
                            }
                            Integer current_level = mt.getEnchantmentLevel(ench_text);
                            if (current_level==null || current_level<ench_level){
                                new_lore_enchantments.put(ench_text, ench_level);
                            }
                        }
                    }
                    for (Map.Entry<String, Integer> entry : new_lore_enchantments.entrySet()){
                        main.getLogger().log(Level.FINE, "Set external updated lore enchantment '"+
                                entry.getKey()+"' for tool '"+id+"' to level '"+entry.getValue()+"'!");
                        main.get_db().upgradeToolEnchantment(id, entry.getKey(), entry.getValue());
                    }
                }
            }
            if (id == null){
                boolean has_lore = false;
                for (String lore_line : lore) {
                    if (ChatColor.stripColor(lore_line).equals(p.getName())) {
                        has_lore = true;
                        break;
                    }
                }
                if (!has_lore)
                    return;
                mb = MendingBlueprint.findBlueprint(itemStack, main);
                if (mb == null)
                    id = main.get_db().add_tool(itemStack, p.getUniqueId().toString(), -1);
                else
                    id = main.get_db().add_tool(itemStack, p.getUniqueId().toString(), mb);
                if (id < 1)
                    return;
                meta.getPersistentDataContainer().set(main.getNBT_key(), PersistentDataType.LONG, id);
                itemStack.setItemMeta(meta);
            }
        };
        p.getInventory().forEach(item_register);
        p.getEnderChest().forEach(item_register);
        for (MendingTool tool : tools.values()) {
            if (!tool.isBroken()) {
                main.get_db().break_tool(tool.getID());
            }
        }
        return;
    }
}
