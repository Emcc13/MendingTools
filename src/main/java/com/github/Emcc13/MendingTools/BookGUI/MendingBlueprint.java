package com.github.Emcc13.MendingTools.BookGUI;

import com.github.Emcc13.MendingTools.Commands.MendingToolsCMD;
import com.github.Emcc13.MendingTools.Listener.MTListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;


public class MendingBlueprint {
    public class MTEnchantment {
        private String enchantment;
        private Integer level;
        private Integer maxlevel = 0;
        private List<String> commands = null;
        private String money = null;

        public MTEnchantment(Element enchNode) {
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

    public MendingBlueprint(Element root) {
        this.id = Integer.parseInt(root.getAttribute("id"));
        this.name = root.getAttribute("name");
        this.material = Material.getMaterial(root.getAttribute("material"));
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
                default:
            }
        }
        NodeList nodes = root.getElementsByTagName("enchantment");
        Node node;
        Element element;
        MTEnchantment enchantment;
        this.enchantments = new HashMap<String, MTEnchantment>();
        for (int idx = 0; idx < nodes.getLength(); idx++) {
            node = nodes.item(idx);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) nodes.item(idx);
            enchantment = new MTEnchantment(element);
            this.enchantments.put(enchantment.enchantment, enchantment);
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
        ItemMeta im = result.getItemMeta();
        List<String> lore = new LinkedList<>();
        for (MTEnchantment enchantment : this.enchantments.values()) {
            try{
                result.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantment.enchantment)), enchantment.level);
            }catch (Exception e){
                lore.add(ChatColor.translateAlternateColorCodes('&',enchantment.enchantment+" "+ MTListener.int2roman(enchantment.level)));
            }
        }
        if (lore.size()>0){
            lore.add("");
        }
        im = result.getItemMeta();
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

    public boolean upgradeAllowed(Enchantment enchantment, int targetLevel) {
        return upgradeAllowed(enchantment.getKey().getKey(), targetLevel);
    }

    public boolean upgradeAllowed(String enchantment, int targetLevel) {
        MTEnchantment ench = this.enchantments.get(enchantment);
        if (ench == null)
            return false;
        return (targetLevel <= ench.maxlevel);
    }

}
