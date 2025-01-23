package com.JIRSendApp.model.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Row {
    HashMap<String, Object> values;

    public Row(HashMap<String, Object> values) {
        this.values = values;
    }

    public Row() {
        this(new HashMap<>());
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public Collection<Object> getValues() {
        return values.values();
    }

    public Set<String> getKeys() {
        return values.keySet();
    }

    public HashMap<String, Object> getMap() {
        return values;
    }

    public void setValue(HashMap<String, Object> values) {
        this.values = values;
    }

    public void add(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public String toString() {
        String rtn = " | ";
        for (String k : values.keySet()) {
            Object o = values.get(k);
            rtn += k + ": " + o.toString();
            rtn += " | ";
        }
        return rtn + "\n";
    }
}
