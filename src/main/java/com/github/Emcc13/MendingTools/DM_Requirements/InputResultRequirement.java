package com.github.Emcc13.MendingTools.DM_Requirements;

import com.github.Emcc13.MendingToolsMain;

import java.util.logging.Level;

public class InputResultRequirement
        extends Requirement {
    private final String input;
    private final String result;
    private final RequirementType type;

    public InputResultRequirement(RequirementType type, String input, String result) {
        this.input = input;
        this.result = result;
        this.type = type;
    }
    public boolean evaluate(MenuHolder holder) {
        double in, res;
        String parsedInput = holder.setPlaceholders(this.input);
        String parsedResult = holder.setPlaceholders(this.result);

        switch (this.type) {
            case STRING_CONTAINS:
                return parsedInput.contains(parsedResult);
            case STRING_EQUALS:
                return parsedInput.equals(parsedResult);
            case STRING_EQUALS_IGNORECASE:
                return parsedInput.equalsIgnoreCase(parsedResult);
            case STRING_DOES_NOT_CONTAIN:
                return !parsedInput.contains(parsedResult);
            case STRING_DOES_NOT_EQUAL:
                return !parsedInput.equals(parsedResult);
            case STRING_DOES_NOT_EQUAL_IGNORECASE:
                return !parsedInput.equalsIgnoreCase(parsedResult);
        }

        try {
            in = Double.parseDouble(parsedInput);
        } catch (NumberFormatException exception) {
            MendingToolsMain.getInstance().getLogger().log(Level.SEVERE, "Input for comparison requirement is an invalid number: " + parsedInput, exception);
               return false;
        }

        try {
            res = Double.parseDouble(parsedResult);
        } catch (NumberFormatException exception) {
            MendingToolsMain.getInstance().getLogger().log(Level.SEVERE, "Output for comparison requirement is an invalid number: " + parsedResult, exception);
            return false;
        }

        switch (this.type) {
            case GREATER_THAN:
                return (in > res);
            case GREATER_THAN_EQUAL_TO:
                return (in >= res);
            case EQUAL_TO:
                return (in == res);
            case LESS_THAN_EQUAL_TO:
                return (in <= res);
            case LESS_THAN:
                return (in < res);
        }
        return false;
    }
}
