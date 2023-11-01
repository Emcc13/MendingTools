package com.github.Emcc13.MendingTools.DM_Requirements;

import com.github.Emcc13.MendingTools.DM_Requirements.wrappers.ItemWrapper;
import com.github.Emcc13.MendingTools.Util.LocationUtils;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class Requirement
{
    private Player successHandler;
    private Player denyHandler;
    private boolean optional;

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%((?<identifier>[a-zA-Z0-9]+)_)(?<parameters>[^%]+)%");

    public Requirement() {
        setOptional(false);
    }

    public Requirement(boolean optional) {
        setOptional(optional);
    }

    public static Requirement getRequirement(Element element) {
        Requirement result = null;
        Function<String, Integer> getInt = str -> {
          try{
              return Integer.parseInt(str);
          } catch (NumberFormatException | NullPointerException e){
              return null;
          }
        };
        Function<String, String> getMatStr = str -> {
            try{
                Material.valueOf(str);
                return str;
            }catch (IllegalArgumentException e){
                return null;
            }
        };
        Function<String, Short> getShort = str -> {
            try{
                return Short.parseShort(str);
            }catch (NumberFormatException | NullPointerException e){
                return null;
            }
        };

        String str_type = getAttributeOrContent(element, "type");
        RequirementType type = RequirementType.getType(str_type);
        ItemWrapper wrapper;
        String att_cont;
        boolean invert;
        switch (type){
            case NULL:
                break;
            case HAS_ITEM:
            case DOES_NOT_HAVE_ITEM:
                wrapper = new ItemWrapper();
                String mat = getMatStr.apply(getAttributeOrContent(element, "material"));
                if (mat != null){
                    wrapper.setMaterial(mat);
                }
                Short data = getShort.apply(getAttributeOrContent(element, "data"));
                wrapper.hasData(data != null);
                wrapper.setData(data == null ? 0 : data);
                
                att_cont = getAttributeOrContent(element, "name");
                if (!att_cont.isEmpty())
                    wrapper.setName(att_cont);

                wrapper.setLoreList(getStringOrList(element, "lore"));

                wrapper.setStrict(Boolean.parseBoolean(getAttributeOrContent(element, "strict")));

                wrapper.setArmor(Boolean.parseBoolean(getAttributeOrContent(element, "armore")));
                wrapper.setOffhand(Boolean.parseBoolean(getAttributeOrContent(element, "offhand")));

                att_cont = getAttributeOrContent(element, "model_data");
                if (isInt(att_cont)){
                    wrapper.setCustomData(Integer.parseInt(att_cont));
                }else{
                    wrapper.setCustomData(0);
                }

                wrapper.setNameContains(
                        Boolean.parseBoolean(getAttributeOrContent(element, "name_contains")) ||
                                Boolean.parseBoolean(getAttributeOrContent(element, "name-contains"))
                );

                wrapper.setNameIgnoreCase(
                        Boolean.parseBoolean(getAttributeOrContent(element, "name_ignorecase")) ||
                                Boolean.parseBoolean(getAttributeOrContent(element, "name-ignorecase"))
                );

                wrapper.setLoreContains(
                        Boolean.parseBoolean(getAttributeOrContent(element, "lore_contains")) ||
                                Boolean.parseBoolean(getAttributeOrContent(element, "lore-contains"))
                );

                wrapper.setLoreIgnoreCase(
                        Boolean.parseBoolean(getAttributeOrContent(element, "lore_ignorecase")) ||
                                Boolean.parseBoolean(getAttributeOrContent(element, "lore-ignorecase"))
                );

                invert = (type == RequirementType.DOES_NOT_HAVE_ITEM);
                result = new HasItemRequirement(wrapper, invert);
                break;
            case HAS_PERMISSION:
            case DOES_NOT_HAVE_PERMISSION:
                invert = (type == RequirementType.DOES_NOT_HAVE_PERMISSION);
                result = new HasPermissionRequirement(getAttributeOrContent(element, "permission"), invert);
                break;

            case EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
            case STRING_CONTAINS:
            case STRING_EQUALS:
            case STRING_EQUALS_IGNORECASE:
            case STRING_DOES_NOT_CONTAIN:
            case STRING_DOES_NOT_EQUAL:
            case STRING_DOES_NOT_EQUAL_IGNORECASE:
                result = new InputResultRequirement(type, getAttributeOrContent(element, "input"), getAttributeOrContent(element, "output"));
                break;

            case HAS_MONEY:
            case DOES_NOT_HAVE_MONEY:
                att_cont = getAttributeOrContent(element, "amount");
                invert = (type == RequirementType.DOES_NOT_HAVE_MONEY);
                if (att_cont!=null && att_cont.matches("^[+-]?\\d+(.\\d+)?$")){
                    result = new HasMoneyRequirement(Double.parseDouble(att_cont), invert, getAttributeOrContent(element, "placeholder"));
                }
                break;

            case HAS_EXP:
            case DOES_NOT_HAVE_EXP:
                att_cont = getAttributeOrContent(element, "amount");
                if (containsPlaceholders(att_cont) || isInt(att_cont)){
                    invert = (type == RequirementType.DOES_NOT_HAVE_EXP);
                    result = new HasExpRequirement(att_cont, invert, Boolean.parseBoolean(getAttributeOrContent(element, "level")));
                }
                break;

            case REGEX_MATCHES:
            case REGEX_DOES_NOT_MATCH:
                if (getAttributeOrContent(element, "input") != null && getAttributeOrContent(element, "regex") != null){
                    Pattern p = Pattern.compile(getAttributeOrContent(element, "regex"));
                    invert = (type == RequirementType.REGEX_DOES_NOT_MATCH);
                    result = new RegexMatchesRequirement(p, getAttributeOrContent(element, "input"), invert);
                }
                break;

            case IS_NEAR:
            case IS_NOT_NEAR:
                if (getAttributeOrContent(element, "location") != null && getAttributeOrContent(element, "distance") != null){
                    invert = (type == RequirementType.IS_NOT_NEAR);
                    Location loc = LocationUtils.deserializeLocation(getAttributeOrContent(element, "location"));
                    result = new IsNearRequirement(loc, Integer.parseInt(getAttributeOrContent(element, "distance")), invert);
                }
                break;

            case HAS_META:
            case DOES_NOT_HAVE_META:
                if (getAttributeOrContent(element, "key") != null &&
                        getAttributeOrContent(element, "meta_type") != null &&
                        getAttributeOrContent(element, "value") != null
                ){
                    invert = (type == RequirementType.DOES_NOT_HAVE_META);
                    result = new HasMetaRequirement(
                            getAttributeOrContent(element, "key"),
                            getAttributeOrContent(element, "meta_type"),
                            getAttributeOrContent(element, "value"),
                            invert
                            );
                }
                break;
        }
        return result;
    }


    public static String getAttributeOrContent(Element node, String key){
        String result = node.getAttribute(key);
        NodeList subNodes;
        if (result == null || result.isEmpty()){
            subNodes = node.getElementsByTagName(key);
            if (subNodes.getLength()>0){
                result = subNodes.item(0).getTextContent();
            }
        }
        return result;
    }

    public static List<String> getStringOrList(Element node, String key){
        List<String> result = new LinkedList<>();
        String attr = node.getAttribute(key);
        if (attr!=null)
            result.add(attr);
        NodeList subNodes;
        if (result.size()<1){
            subNodes = node.getElementsByTagName(key);
            for (int idx = 0; idx < subNodes.getLength(); idx++){
                result.add(subNodes.item(idx).getTextContent());
            }
        }
        return result;
    }

    public static boolean isInt(String int_str){
        return int_str!=null && int_str.matches("^[+-]?\\d+$");
    }

    public static boolean containsPlaceholders(String text) {
        return PLACEHOLDER_PATTERN.matcher(text).find();
    }

    public abstract boolean evaluate(MenuHolder paramMenuHolder);

    public Player getDenyHandler() {
        return this.denyHandler;
    }

    public void setDenyHandler(Player denyHandler) {
        this.denyHandler = denyHandler;
    }

    public boolean hasDenyHandler() {
        return (this.denyHandler != null);
    }

    public boolean isOptional() {
        return this.optional;
    }

    public MendingToolsMain getInstance() {
        return MendingToolsMain.getInstance();
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Player getSuccessHandler() {
        return this.successHandler;
    }

    public void setSuccessHandler(Player successHandler) {
        this.successHandler = successHandler;
    }
}
