package com.JIRSend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Table {
    private Connection connection;
    private String name;
    private boolean autoFetch;
    private HashMap<String, Column<Object>> columns;
    private ArrayList<HashMap<String, Object>> rows;

    public Table(Connection connection, String name, ArrayList<Pair<String>> columnsAndTypes, boolean autoFetch) {
        this.connection = connection;
        this.name = name;
        this.autoFetch = autoFetch;
        this.columns = new HashMap<>();
        this.rows = new ArrayList<>();

        for (Pair<String> cat : columnsAndTypes) {
            this.columns.put(cat.left, new Column<>(cat.left, cat.right));
        }
    }

    public Table(Connection connection, String name, ArrayList<Pair<String>> columnsAndTypes) {
        this(connection, name, columnsAndTypes, false);
    }

    public String getName() {
        return this.name;
    }

    /**
     * Define whether each time you get values from the table it fetches database
     * (default = false)
     * 
     * @param autoFetch
     */
    public void setAutoFetch(boolean autoFetch) {
        this.autoFetch = autoFetch;
    }


    public ArrayList<Column<Object>> getColumns() {
        if (autoFetch)
            fetch();
        ArrayList<Column<Object>> cs = new ArrayList<>();
        for (Column<Object> c : columns.values()) {
            cs.add(c);
        }
        return cs;
    }

    /**
     * @deprecated since we are not caching anymore
     * @return row names
     */
    public ArrayList<String> getColumnsName() {
        ArrayList<String> cs = new ArrayList<>();
        for (String c : columns.keySet()) {
            cs.add(c);
        }
        return cs;
    }

    /**
     * @deprecated since we are not caching anymore
     * @return rows
     */
    public ArrayList<HashMap<String, Object>> getRows() {
        if (autoFetch)
            fetch();
        return rows;
    }

    /**
     * @deprecated since we are not caching anymore
     * @param index
     * @return
     */
    public HashMap<String, Object> getRow(int index) {
        if (autoFetch)
            fetch();
        return rows.get(index);
    }

    public int rowsCount() {
        if (autoFetch)
            fetch();
        return rows.size();
    }

    @Override
    public String toString() {
        String rtn = "--- TABLE: " + getName() + " ---\n";
        for (Column<Object> c : getColumns()) {
            rtn += " | " + c.getName() + " (" + c.getTypeName() + ")";
        }
        // rtn += (this.columns.size() > 0 ? " |" : "") + "\n--- " + rows.size() + " elements ---\n";

        // for (HashMap<String, Object> hm : rows) {
        //     for (Object v : hm.values()) {
        //         rtn += " | " + v.toString();
        //     }
        // }

        rtn += (this.columns.size() > 0 ? " |\n" : "\n");
        return rtn;
    }

    /**
     * Execute a select on the current table
     * 
     * @deprecated since we are not caching anymore
     * @param what
     * @param condition ex: "ID=? AND val=1"
     * @param variables the values that will replace the "?" in the condition
     */
    public ArrayList<HashMap<String, Object>> select(String what, String condition,
            ArrayList<Object> variables) {
        String sql = "SELECT " + what + " FROM " + this.name + condition != "" ? (" WHERE " + condition) : "";
        try {
            PreparedStatement p = connection.prepareStatement(sql);
            int i = 0;
            for (Object v : variables) {
                if (v instanceof String)
                    p.setString(i++, (String) v);
                else if (v instanceof Integer)
                    p.setInt(i, i);
                // p.setBigDecimal(i, null);

                // p.setBig
            }
            ResultSet rs = p.executeQuery(sql);
            if (rs == null)
                return rows;

            // reset actual data
            rows = new ArrayList<>();
            for (Column<Object> c : columns.values()) {
                c.resetValues();
            }
            // res
            while (rs.next()) {
                HashMap<String, Object> r = new HashMap<>();
                for (String key : columns.keySet()) {
                    columns.get(key).addValue(rs.getString(key));
                    r.put(key, rs.getString(key));
                }
                rows.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public ArrayList<HashMap<String, Object>> select(String what) {
        return select(what, "", new ArrayList<>());
    }

    /**
     * Update the table values
     */
    public void fetch() {
        select("*");
    }

    /**
     * @deprecated since we are not caching anymore
     * @param what
     * @param values
     * @return
     */
    public int insert(String what, String values) {
        String sql = "INSERT INTO "+ this.name + " " + what + " VALUES " + values;
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
