package com.github.Emcc13.MendingTools.Config;

import java.util.List;
import java.util.Map;

public enum DE_language implements ConfigInterface{

    ;
    public final List<Map<String, String>> value;
    DE_language(List<Map<String, String>> value) {
        this.value = value;
    }
    public Object value(){
        return this.value;
    }
    public String key(){
        return this.name().replace('_','.');
    }
}
