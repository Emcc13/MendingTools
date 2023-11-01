package com.github.Emcc13.MendingTools.DM_Requirements;

import java.util.regex.Pattern;

public class RegexMatchesRequirement
        extends Requirement {
    private final Pattern pattern;
    private final String input;
    private final boolean invert;

    public RegexMatchesRequirement(Pattern pattern, String input, boolean invert) {
        this.pattern = pattern;
        this.input = input;
        this.invert = invert;
    }
    public boolean evaluate(MenuHolder holder) {
        String toCheck = holder.setPlaceholders(this.input);
        if (this.invert) {
            return !this.pattern.matcher(holder.setPlaceholders(toCheck)).find();
        }
        return this.pattern.matcher(holder.setPlaceholders(toCheck)).find();
    }
}
