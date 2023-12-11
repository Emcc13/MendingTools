package com.github.Emcc13.MendingTools.Listener;

import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.Util.InventoryUtil;
import com.github.Emcc13.MendingToolsMain;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedList;
import java.util.List;

public class MTListener implements Listener {
    private MendingToolsMain main;

    public MTListener(MendingToolsMain main) {
        this.main = main;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        InventoryUtil.scanPlayerInventory(p);
        return;
    }

    @EventHandler
    void onItemBreakEvent(PlayerItemBreakEvent event) {
        ItemStack item = event.getBrokenItem();
        ItemMeta im = item.getItemMeta();
        Long id = im.getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
        if (id != null) {
            main.get_db().break_tool(id);
        }
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
        ItemStack item = event.getCursor();
        boolean checkItem = false;
        Inventory inv = event.getClickedInventory();
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                inv = event.getInventory();
                item = event.getCurrentItem();
                switch (inv.getType()) {
                    case ENDER_CHEST:
                    case PLAYER:
                    case SMITHING:
                        return;
                    case ANVIL:
                        if (ArrayUtils.indexOf(inv.getContents(), null) > 0)
                            checkItem = true;
                        break;
                    default:
                        checkItem = true;
                }
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR:
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
                    default:
                        checkItem = true;
                }
                break;
            case CLONE_STACK:
            case DROP_ALL_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_CURSOR:
            case DROP_ONE_SLOT:
                checkItem = true;
                break;
            case HOTBAR_SWAP:
            case HOTBAR_MOVE_AND_READD:
                switch (event.getClickedInventory().getType()){
                    case PLAYER:
                    case ENDER_CHEST:
                    case SMITHING:
                        return;
                    default:
                        item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                        checkItem = true;
                        break;
                }
                break;
        }
        if (checkItem && item != null && item.hasItemMeta() &&
                item.getItemMeta().getPersistentDataContainer().
                get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
//        if (!(event.getRightClicked() instanceof ItemFrame)){
//            return;
//        }
        if (event.getRightClicked() instanceof Sheep ||
                event.getRightClicked() instanceof MushroomCow ||
                event.getRightClicked() instanceof Snowman
        )
            return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR)
            item = event.getPlayer().getInventory().getItemInOffHand();
        if (item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().
                get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
//        if (!(event.getRightClicked() instanceof ItemFrame)){
//            return;
//        }
//        if (event.getRightClicked() instanceof Sheep ||
//                event.getRightClicked() instanceof MushroomCow ||
//                event.getRightClicked() instanceof Snowman
//        )
//            return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR)
            item = event.getPlayer().getInventory().getItemInOffHand();
        if (item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().
                get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event){
        ItemStack itemOnCursor = event.getPlayer().getItemOnCursor();
        if (itemOnCursor.getType() == Material.AIR ||
                itemOnCursor.getItemMeta() == null ||
                itemOnCursor.getItemMeta().getPersistentDataContainer()
                        .get(main.getNBT_key(), PersistentDataType.LONG) == null
        )
            return;
        if (!has_free_slot(event.getPlayer())){
            // TODO: maybe reopen instead of marking it broken
            Long id = itemOnCursor.getItemMeta().getPersistentDataContainer()
                    .get(main.getNBT_key(), PersistentDataType.LONG);
            if (id != null) {
                main.get_db().break_tool(id);
            }
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
            case CRAFTING:
                return;
        }
        ItemStack is = event.getOldCursor();
        if (is != null && is.getType() != Material.AIR &&
        is.getItemMeta() != null &&
                is.getItemMeta().getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG) != null) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        ItemStack is = event.getItemDrop().getItemStack();
        if (is == null || is.getType() == Material.AIR)
            return;
        ItemMeta im = event.getItemDrop().getItemStack().getItemMeta();
        if (im==null)
            return;
        Long id = im.getPersistentDataContainer().get(main.getNBT_key(), PersistentDataType.LONG);
        if (id != null) {
            event.getItemDrop().remove();
            event.setCancelled(true);
        }
    }

    public static boolean has_free_slot(HumanEntity player){
        for (ItemStack is : player.getInventory().getStorageContents()){
            if (is == null)
                return true;
            if (is.getType() == Material.AIR)
                return true;
        }
        return false;
    }

    public static String int2roman(Integer value){
        if (value==null)
            return "";
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[value / 1000] + hundreds[(value % 1000) / 100] + tens[(value % 100) / 10] + ones[value % 10];
    }
}
