package com.github.Emcc13.MendingTools.DM_Requirements;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum RequirementType {
    HAS_META(Arrays.asList("has meta", "meta"), "Checks if a player has a certain metadata value",
            Arrays.asList("key", "value")),
    DOES_NOT_HAVE_META(Arrays.asList("!has meta", "!meta"), "Checks if a player does not have a certain metadata value",
            Arrays.asList("key", "value")),

    IS_NEAR(Arrays.asList("is near", "near"), "Checks if a player is within a certain distance of a specific location",
            Arrays.asList("location", "distance")),
    IS_NOT_NEAR(Arrays.asList("!is near", "!near"), "Checks if a player is not within a certain distance of a specific location",
            Arrays.asList("location", "distance")),

    HAS_ITEM(Arrays.asList("has item", "item", "hasitem"), "Checks if a player has a specific item",
            Arrays.asList("material", "amount", "data", "name", "lore")),
    DOES_NOT_HAVE_ITEM(Arrays.asList("!has item", "!item", "!hasitem", "does not have item"), "Checks if a player does not have specific item",
            Arrays.asList("material", "amount", "data", "name", "lore")),

    HAS_MONEY(Arrays.asList("has money", "hasmoney", "money"), "Checks if a player has enough money (Vault required)",
            Arrays.asList("amount", "placeholder")),
    DOES_NOT_HAVE_MONEY(Arrays.asList("!has money", "!hasmoney", "!money"), "Checks if a player does not have enough money (Vault required)",
            Arrays.asList("amount", "placeholder")),

    HAS_EXP(Arrays.asList("has exp", "hasexp", "exp"), "Checks if a player has enough exp",
            Collections.singletonList("amount")),
    DOES_NOT_HAVE_EXP(Arrays.asList("!has exp", "!hasexp", "!exp"), "Checks if a player has enough exp",
            Collections.singletonList("amount")),

    HAS_PERMISSION(Arrays.asList("has permission", "has perm", "haspermission", "hasperm", "perm"), "Checks if a player has a specific permission",
            Collections.singletonList("permission")),
    DOES_NOT_HAVE_PERMISSION(
            Arrays.asList("!has permission", "!has perm", "!haspermission", "!hasperm", "!perm"), "Checks if a player does not have a specific permission",
            Collections.singletonList("permission")),

    STRING_CONTAINS(Arrays.asList("string contains", "stringcontains", "contains"), "Checks if a string contains another string",
            Arrays.asList("input", "output")),
    STRING_DOES_NOT_CONTAIN(Arrays.asList("!string contains", "!stringcontains", "!contains"), "Checks if a string does not contain another string",
            Arrays.asList("input", "output")),

    STRING_EQUALS(Arrays.asList("string equals", "stringequals", "equals"), "Checks if a string equals another string",
            Arrays.asList("input", "output")),
    STRING_DOES_NOT_EQUAL(Arrays.asList("!string equals", "!stringequals", "!equals"), "Checks if a string does not equal another string",
            Arrays.asList("input", "output")),

    STRING_EQUALS_IGNORECASE(
            Arrays.asList("stringequalsignorecase", "string equals ignorecase", "equalsignorecase"), "Checks if a string equals another string ignoring case",
            Arrays.asList("input", "output")),
    STRING_DOES_NOT_EQUAL_IGNORECASE(
            Arrays.asList("!stringequalsignorecase", "!string equals ignorecase", "!equalsignorecase"), "Checks if a string does not equal another string ignoring case",
            Arrays.asList("input", "output")),

    GREATER_THAN(Arrays.asList(">", "greater than", "greaterthan"), "Checks if a number is greater than another number",
            Arrays.asList("input", "output")),
    GREATER_THAN_EQUAL_TO(Arrays.asList(">=", "greater than or equal to", "greaterthanorequalto"), "Checks if a number is greater than or equal to another number",
            Arrays.asList("input", "output")),
    EQUAL_TO(Arrays.asList("==", "equal to", "equalto"), "Checks if a number is equal to another number",
            Arrays.asList("input", "output")),
    LESS_THAN_EQUAL_TO(Arrays.asList("<=", "less than or equal to", "lessthanorequalto"), "Checks if a number is less than or equal to another number",
            Arrays.asList("input", "output")),
    LESS_THAN(Arrays.asList("<", "less than", "lessthan"), "Checks if a number is less than another number",
            Arrays.asList("input", "output")),

    REGEX_MATCHES(Arrays.asList("regex matches", "regex"), "Checks if a placeholder parsed string matches a regex pattern",
            Arrays.asList("input", "regex")),
    REGEX_DOES_NOT_MATCH(Arrays.asList("!regex matches", "!regex"), "Checks if a placeholder parsed string does not match a regex pattern",
            Arrays.asList("input", "regex")),

    NULL(null, null, null);

    private final List<String> identifier;
    private final String description;
    private final List<String> configOptions;

    RequirementType(List<String> identifier, String description, List<String> options) {
        this.identifier = identifier;
        this.description = description;
        this.configOptions = options;
    }

    public static RequirementType getType(String s) {
        for (RequirementType type : values()) {
            if (type.getIdentifiers() == null)
                return NULL;
            for (String id : type.getIdentifiers()) {
                if (s.equalsIgnoreCase(id)) {
                    return type;
                }
            }
        }
        return NULL;
    }

    public List<String> getIdentifiers() {
        return this.identifier;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getConfigOptions() {
        return this.configOptions;
    }
}
