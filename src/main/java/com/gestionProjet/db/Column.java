package com.gestionProjet.db;

import java.util.ArrayList;

public class Column<T> {
    private String name;
    private String type;
    private ArrayList<T> values;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
        this.values = new ArrayList<>();
    }

    /**
     * @deprecated since we are not caching anymore
     */
    public void resetValues() {
        values = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type;
    }

    /**
     * @deprecated since we are not caching anymore
     * @return values
     */
    public ArrayList<T> getValues() {
        return values;
    }

    /**
     * @deprecated since we are not caching anymore
     * @param index
     * @return value at index
     */
    public T getValue(int index) {
        return values.get(index);
    }

    /**
     * @deprecated since we are not caching anymore
     * @param value
     */
    public void addValue(T value) {
        values.add(value);
    }

    /**
     * @deprecated since we are not caching anymore
     * @return
     */
    public int valuesCount() {
        return values.size();
    }
}
