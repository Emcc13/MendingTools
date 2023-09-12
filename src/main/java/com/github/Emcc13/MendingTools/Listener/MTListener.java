package com.github.Emcc13.MendingTools.Listener;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingToolsMain;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
            boolean has_lore = false;
            List<String> lore = meta.getLore();
            if (lore != null) {
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
                if (!has_lore) {
                    meta.setLore(new ArrayList<String>() {{
                        add(p.getName());
                    }});
                    has_lore = true;
                }
            }
            if (!has_lore)
                return;
            MendingBlueprint mb = findBlueprint(itemStack);
            if (mb == null)
                id = main.get_db().add_tool(itemStack, p.getUniqueId().toString(), -1);
            else
                id = main.get_db().add_tool(itemStack, p.getUniqueId().toString(), mb);
            if (id < 1)
                return;
            meta.getPersistentDataContainer().set(main.getNBT_key(), PersistentDataType.LONG, id);
            itemStack.setItemMeta(meta);
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
        if (!(event.getRightClicked() instanceof ItemFrame)){
            return;
        }
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
        if (!(event.getRightClicked() instanceof ItemFrame)){
            return;
        }
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
            // TODO: maybe reopen instead mark it broken
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

    private MendingBlueprint findBlueprint(ItemStack itemStack) {
        Map<String, Integer> isEnch = itemStack.getEnchantments().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey().getKey(), Map.Entry::getValue));
        for (String line : itemStack.getItemMeta().getLore()){
            if (line.startsWith("&7")){
                String[]ench = line.split(" ");
                try {
                    isEnch.put(ench[0], roman2int(ench[1]));
                }catch (Exception e){}
            }
        }

        boolean netherite_equal_diamond = (boolean) main.getCachedConfig().get(BaseConfig_EN.option_netherite_equal_diamond.key());
        MendingBlueprint last_bp=null;
        Integer last_dist=null;
        Integer ench_lvl;
        for (MendingBlueprint mb : main.getBlueprintConfig().getBlueprints().values()) {
            String[] mb_mat = mb.getMaterial().split("_");
            String[] is_mat = itemStack.getType().name().split("_");
            if ((mb.getMaterialType() != itemStack.getType()) &&
                    !(netherite_equal_diamond &&
                            mb_mat[0].toLowerCase().equals("diamond") &&
                            is_mat[0].toLowerCase().equals("netherite") &&
                            mb_mat[1].toLowerCase().equals(is_mat[1].toLowerCase())
                    ))
                continue;
            boolean isOk = true;
            for (MendingBlueprint.MTEnchantment ench : mb.getEnchantments()) {
                ench_lvl = isEnch.get(ench.getEnchantment());
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

    public static boolean has_free_slot(HumanEntity player){
        for (ItemStack is : player.getInventory().getStorageContents()){
            if (is == null)
                return true;
            if (is.getType() == Material.AIR)
                return true;
        }
        return false;
    }

    private static int roman2value(char r){
        r = Character.toLowerCase(r);
        switch (r){
            case 'i':
                return 1;
            case 'v':
                return 5;
            case 'x':
                return 10;
            case 'l':
                return 50;
            case 'c':
                return 100;
            case 'd':
                return 500;
            case 'm':
                return 1000;
            default:
                return 0;
        }
    }

    public static int roman2int(String roman){
        int result = 0;
        roman = roman.toLowerCase();
        int last_value = 0;
        int new_value = 0;
        for (int idx = 0; idx<roman.length(); idx++){
            new_value = roman2value(roman.charAt(idx));
            if (last_value<new_value)
                result -= 2*last_value;
            result += new_value;
            last_value = new_value;
        }
        return result;
    }

    public static String int2roman(int value){
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[value / 1000] + hundreds[(value % 1000) / 100] + tens[(value % 100) / 10] + ones[value % 10];
    }
}
