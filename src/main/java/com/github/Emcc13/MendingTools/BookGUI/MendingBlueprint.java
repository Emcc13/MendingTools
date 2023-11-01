package com.github.Emcc13.MendingTools.BookGUI;

import com.github.Emcc13.MendingTools.Commands.MendingToolsCMD;
import com.github.Emcc13.MendingTools.Config.BaseConfig_EN;
import com.github.Emcc13.MendingTools.DM_Requirements.MenuHolder;
import com.github.Emcc13.MendingTools.DM_Requirements.Requirement;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import com.github.Emcc13.MendingToolsMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class MendingBlueprint {
    public class MTEnchantment {
        private String enchantment;
        private Integer level;
        private Integer maxlevel = 0;
        private List<String> commands = null;
        private String money = null;
        private List<Requirement> requirements = null;

        public MTEnchantment(Element enchNode) {
            this.requirements = new LinkedList<>();
            try{
                Enchantment mc_enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchNode.getAttribute("name")));
                this.enchantment = mc_enchantment.getKey().getKey();
            }catch (Exception e){
                this.enchantment = enchNode.getAttribute("name");
            }
            this.level = Integer.parseInt(enchNode.getAttribute("level"));
            String mlString = enchNode.getAttribute("maxlevel");
            if (mlString.length() > 0) {
                this.maxlevel = Integer.parseInt(enchNode.getAttribute("maxlevel"));
            } else {
                this.maxlevel = this.level;
            }
            NodeList moneyCalc = enchNode.getElementsByTagName("money");
            if (moneyCalc.getLength() > 0) {
                this.money = moneyCalc.item(0).getTextContent();
            }
            NodeList commandsNL = enchNode.getElementsByTagName("command");
            if (commandsNL.getLength() > 0) {
                this.commands = new LinkedList<>();
                for (int idx = 0; idx < commandsNL.getLength(); idx++) {
                    this.commands.add(commandsNL.item(idx).getTextContent());
                }
            }
            NodeList requirements_list = enchNode.getElementsByTagName("requirement");
            for (int idx = 0; idx < requirements_list.getLength(); idx++){
                this.requirements.add(Requirement.getRequirement((Element)requirements_list.item(idx)));
            }
        }

        public String getEnchantment() {
            return enchantment;
        }

        public Integer getLevel() {
            return level;
        }

        public Integer getMaxlevel(){
            return maxlevel;
        }

        public List<String> getCommands() {
            return commands;
        }

        public String getMoney() {
            return money;
        }

        public List<Requirement> getRequirements(){
            return requirements;
        }

        public void addToConf(Element root, Document doc) {
            Element enchantment = doc.createElement("enchantment");
            enchantment.setAttribute("name", this.enchantment);
            enchantment.setAttribute("level", this.level.toString());
            enchantment.setAttribute("maxlevel", this.maxlevel.toString());
            if (this.money != null) {
                Element money = doc.createElement("money");
                money.setTextContent(this.money);
                enchantment.appendChild(money);
            }
            if (this.commands != null) {
                Element command;
                for (String commandString : this.commands) {
                    command = doc.createElement("command");
                    command.setTextContent(commandString);
                    enchantment.appendChild(command);
                }
            }
            root.appendChild(enchantment);
        }
    }

    private int id;
    private String name;
    private Material material;
    private String money = null;
    private List<String> commands = null;
    private Map<String, MTEnchantment> enchantments;
    private List<Requirement> requirements = null;

    public MendingBlueprint(Element root) {
        this.id = Integer.parseInt(root.getAttribute("id"));
        this.name = root.getAttribute("name");
        this.material = Material.getMaterial(root.getAttribute("material"));
        Element element;
        MTEnchantment enchantment;
        Requirement requirement;
        this.enchantments = new HashMap<String, MTEnchantment>();
        this.requirements = new LinkedList<>();
        NodeList childNodes = root.getChildNodes();

        for (int idx = 0; idx < childNodes.getLength(); idx++) {
            Node node = childNodes.item(idx);
            switch (node.getNodeName()) {
                case "money":
                    this.money = node.getTextContent();
                    break;
                case "command":
                    if (this.commands == null)
                        this.commands = new LinkedList<>();
                    this.commands.add(node.getTextContent());
                    break;
                case "enchantment":
                    if (node.getNodeType() != Node.ELEMENT_NODE)
                        break;
                    element = (Element) node;
                    enchantment = new MTEnchantment(element);
                    this.enchantments.put(enchantment.enchantment, enchantment);
                    break;
                case "requirement":
                    if (node.getNodeType() != Node.ELEMENT_NODE)
                        break;
                    element = (Element) node;
                    requirement = Requirement.getRequirement(element);
                    this.requirements.add(requirement);
                    break;
                default:
            }
        }
    }

    public List<TextComponent> asPage() {
        List<TextComponent> result = new LinkedList<TextComponent>();
        TextComponent name = new TextComponent(this.name != null ? this.name : "Unnamed"),
                material = new TextComponent(this.material != null ? this.material.name() : "Unknown");
        name.setBold(true);
        result.add(name);
        result.add(new TextComponent("\n"));
        material.setItalic(true);
        result.add(material);
        result.add(new TextComponent("\n"));
        result.add(new TextComponent("ID:   "+this.id+"\n"));
        for (MTEnchantment enchantment : this.enchantments.values()) {
            result.add(new TextComponent("\n"));
            TextComponent enchant = new TextComponent(), level = new TextComponent();
            enchant.setText(enchantment.getEnchantment() + ":   ");
            level.setText(enchantment.getLevel().toString()+" - "+enchantment.getMaxlevel().toString());
            level.setItalic(true);
            enchant.addExtra(level);
            result.add(enchant);
        }
        TextComponent command = new TextComponent("\n\nGet Command");
        command.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
//                "/" + mtToolNew.COMMAND + " " + this.id + " "));
                "/" + MendingToolsCMD.COMMAND + " new " + this.id + " "));
        result.add(command);

        return result;
    }

    public void addToConf(Element root, Document doc) {
        Element blueprint = doc.createElement("blueprint");
        blueprint.setAttribute("id", String.valueOf(this.id));
        blueprint.setAttribute("name", this.name);
        blueprint.setAttribute("material", this.getMaterial());
        if (this.money != null) {
            Element money = doc.createElement("money");
            money.setTextContent(this.money);
            blueprint.appendChild(money);
        }
        if (this.commands != null) {
            for (String command : this.commands) {
                Element elemCommand = doc.createElement("command");
                elemCommand.setTextContent(command);
                blueprint.appendChild(elemCommand);
            }
        }
        for (MTEnchantment enchantment : this.enchantments.values()) {
            enchantment.addToConf(blueprint, doc);
        }
        root.appendChild(blueprint);
    }

    public ItemStack getItemStack() {
        ItemStack result = new ItemStack(this.material);
        List<String> lore = new LinkedList<>();
        for (MTEnchantment enchantment : this.enchantments.values()) {
            if (enchantment.level>0){
                try{
                    result.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantment.enchantment)), enchantment.level);
                }catch (Exception e){

                    lore.add(ChatColor.translateAlternateColorCodes('&',enchantment.enchantment+" "+ MTListener.int2roman(enchantment.level)));
                }
            }
        }
        if (lore.size()>0){
            lore.add("");
        }
        ItemMeta im = result.getItemMeta();
        im.setLore(lore);
        result.setItemMeta(im);
        return result;
    }

    public String getMaterial() {
        return this.material.name();
    }

    public Material getMaterialType(){
        return this.material;
    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public String getMoney() {
        return this.money;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public Collection<MTEnchantment> getEnchantments() {
        return this.enchantments.values();
    }

    public MTEnchantment getEnchantment(Enchantment enchantment) {
        return getEnchantment(enchantment.getKey().getKey());
    }

    public MTEnchantment getEnchantment(String enchantment) {
        return this.enchantments.get(enchantment);
    }

    public boolean isUpgradeable(Enchantment enchantment, int currentLevel) {
        return this.isUpgradeable(enchantment.getKey().getKey(), currentLevel);
    }

    public boolean isUpgradeable(String enchantment, int currentLevel) {
        MTEnchantment ench = this.enchantments.get(enchantment);
        if (ench == null)
            return false;
        return currentLevel < ench.maxlevel;
    }

    public boolean upgradeAllowed(Player p, Enchantment enchantment, int targetLevel) {
        return upgradeAllowed(p, enchantment.getKey().getKey(), targetLevel);
    }

    public boolean upgradeAllowed(Player p, String enchantment, int targetLevel) {
        MTEnchantment ench = this.enchantments.get(enchantment);
        if (ench == null)
            return false;
        if (targetLevel > ench.maxlevel)
            return false;
        MenuHolder mh = new MenuHolder(p);
        for (Requirement requirement : ench.getRequirements()){
            if (!requirement.evaluate(mh))
                return false;
        }
        return true;
    }

    public boolean checkRequirements(Player p){
        MenuHolder mh = new MenuHolder(p);
        for (Requirement requirement : this.requirements){
            if (!requirement.evaluate(mh))
                return false;
        }
        return true;
    }

    public static MendingBlueprint findBlueprint(ItemStack itemStack, MendingToolsMain main) {
        Map<String, Integer> isEnch = itemStack.getEnchantments().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey().getKey(), Map.Entry::getValue));
        for (String line : itemStack.getItemMeta().getLore()){
            if (line.startsWith("&7")){
                String[]ench = line.split(" ");
                try {
                    isEnch.put(ench[0], ench.length>1?roman2int(ench[1]):1);
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
                if ((ench_lvl!=null &&
                        ench.getMaxlevel()>=ench_lvl &&
                        ench.getLevel()<= ench_lvl
                ) || ench.getLevel() < 1)
                    continue;
                isOk = false;
            }
            if (isOk){
                int dist = abs(isEnch.size()-mb.getEnchantments().size());
                if (last_dist == null || dist < last_dist){
                    last_dist = dist;
                    last_bp = mb;
                }
            }
        }
        return last_bp;
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

}
