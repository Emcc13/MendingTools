package com.github.Emcc13.MendingTools.Listener;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class MTListener implements Listener {
    private MendingToolsMain main;

    public MTListener(MendingToolsMain main) {
        this.main = main;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Enchantment ench = Enchantment.MENDING;
        Map<Long, MendingTool> tools = new HashMap<Long, MendingTool>();
        List<MendingTool> tmp = main.get_db().getPlayerTools(p.getUniqueId().toString());
        if (tmp==null){
            tmp = new LinkedList<>();
        }
        for (MendingTool tool : tmp){
            tools.put(tool.getID(), tool);
        }

        Consumer<ItemStack> item_register = itemStack -> {
            if (itemStack == null)
                return;
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            if (!meta.hasEnchant(ench))
                return;
            boolean has_lore = false;
            List<String> lore = meta.getLore();
            if (lore != null){
                for (String lore_line : lore) {
                    if (ChatColor.stripColor(lore_line).equals(p.getName())) {
                        has_lore = true;
                        break;
                    }
                }
            }
            Long id = meta.getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
            if (id != null) {
                if (tools.containsKey(id)) {
                    tools.remove(id);
                    return;
                }
                if (!has_lore){
                    meta.setLore(new ArrayList<String>(){{
                        add(p.getName());
                    }});
                    has_lore=true;
                }
            }
            if (!has_lore)
                return;
            id = main.get_db().add_tool(itemStack, p.getUniqueId().toString(), findBlueprint(itemStack));
            if (id < 1)
                return;
            meta.getPersistentDataContainer().set(main.getNBT_key(), PersistentDataType.LONG, id);
            itemStack.setItemMeta(meta);
        };
        p.getInventory().forEach(item_register);
        p.getEnderChest().forEach(item_register);
        for (MendingTool tool : tools.values()){
            if (!tool.isBroken()) {
                main.get_db().break_tool(tool.getID());
            }
        }
        return;
    }

    @EventHandler
    boolean onItemBreakEvent(PlayerItemBreakEvent event) {
        ItemStack item = event.getBrokenItem();
        ItemMeta im = item.getItemMeta();
        Long id = im.getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
        if (id != null) {
            main.get_db().break_tool(id);
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        if (p.hasPermission((String) main.getCachedConfig().get(BaseConfig_EN.perm_keep_inventory.key()))) return;
        List<ItemStack> drops = event.getDrops();
        Long id;
        List<ItemStack> toRemove = new LinkedList<>();
        for (ItemStack is : drops) {
            id = is.getItemMeta().getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
            if (id != null) {
                main.get_db().break_tool(id);
                toRemove.add(is);
            }
        }
        for (ItemStack is : toRemove) {
            drops.remove(is);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) return;
        final Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCursor();
        boolean checkItem = false;
        Inventory inv = event.getClickedInventory();
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                inv = event.getInventory();
                item = event.getCurrentItem();
                switch (inv.getType()){
                    case ENDER_CHEST:
                    case PLAYER:
                    case SMITHING:
                        return;
                    case ANVIL:
                        if (ArrayUtils.indexOf(inv.getContents(), null) > 0)
                            checkItem = true;
                        break;
                }
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
                if (inv == null) {
                    checkItem = true;
                    break;
                }
                switch (inv.getType()) {
                    case ENDER_CHEST:
                    case PLAYER:
                    case SMITHING:
                        return;
                    case ANVIL:
                        if (event.getSlot() > 0)
                            checkItem = true;
                        break;
                }
                break;
            case CLONE_STACK:
            case DROP_ALL_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_CURSOR:
            case DROP_ONE_SLOT:
                checkItem = true;
                break;
        }
        if (checkItem && item !=null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().
                get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) return;
        final Player p = (Player) event.getWhoClicked();
        switch (event.getInventory().getType()) {
            case ENDER_CHEST:
            case PLAYER:
            case SMITHING:
                return;
        }
        ItemStack is = event.getOldCursor();
        if (is != null && is.getType() != Material.AIR && is.
                getItemMeta().getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        ItemStack is = event.getItemDrop().getItemStack();
        if (is == null || is.getType() == Material.AIR)
            return;
        Long id = event.getItemDrop().getItemStack().getItemMeta().
                getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
        if (id != null) {
            event.getItemDrop().remove();
            event.setCancelled(true);
        }
    }

    private int findBlueprint(ItemStack itemStack){
        Map<Enchantment, Integer> isEnch = itemStack.getEnchantments();
        for (MendingBlueprint mb : main.getBlueprintConfig().getBlueprints().values()){
            if (mb.getMaterialType() != itemStack.getType())
                continue;
            if (mb.getEnchantments().size() != itemStack.getEnchantments().size()){
                continue;
            }
            boolean isOk = true;
            for (MendingBlueprint.MTEnchantment ench : mb.getEnchantments()){
                if (ench.getMaxlevel()>=isEnch.getOrDefault(ench.getEnchantment(), 0))
                    continue;
                isOk = false;
            }
            for (Map.Entry<Enchantment, Integer> ench_entry: itemStack.getEnchantments().entrySet()){
                if (mb.getEnchantment(ench_entry.getKey())!= null &&
                        ench_entry.getValue()<=mb.getEnchantment(ench_entry.getKey()).getMaxlevel())
                    continue;
                isOk = false;
            }
            if (isOk)
                return mb.getID();
        }
        return -1;
    }
}
