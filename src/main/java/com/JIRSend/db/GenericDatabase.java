package com.JIRSend.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.JIRSend.ui.ErrorPopup;
import com.JIRSend.ui.Log;

public class GenericDatabase {
    private boolean connected;
    private String serverAddress;
    private String database;
    private String user;
    private String password;
    private Connection connection;

    private String catalog, schema;
    private HashMap<String, Table> tables;

    public GenericDatabase(String serverAddress, String database, String user, String password) {
        this.serverAddress = serverAddress.endsWith("/") ? serverAddress : serverAddress + "/";
        this.database = database;
        this.user = user;
        this.password = password;
        this.connected = false;
        tables = new HashMap<>();
        Log.l("Created database " + database);
    }

    /**
     * Connect to database
     * 
     * @return
     */
    public boolean connect() {
        if (connected)
            return true;
        try {
            this.connection = DriverManager.getConnection("jdbc:" + serverAddress + database, user, password);
            if (connection == null)
                return false;

            Log.l("Database connected",Log.LOG);
            catalog = connection.getCatalog();
            schema = connection.getSchema();
            DatabaseMetaData dbmd = connection.getMetaData();
            HashMap<String, ArrayList<Pair<String>>> columnsMap = new HashMap<>();

            // getting tables
            ResultSet tablesRes = dbmd.getTables(catalog, schema, "%", null);

            while (tablesRes.next()) {
                columnsMap.put(tablesRes.getString(3), new ArrayList<>());
                //Log.ll("|" + tablesRes.getString(3), 9);
            }
            //Log.ll("|\n", 9);

            // FOR EACH TABLE
            for (String t : columnsMap.keySet()) {

                // get columns
                ResultSet columnSet = dbmd.getColumns(catalog, schema, t, "%");

                while (columnSet.next()) {
                    columnsMap.get(t).add(new Pair<String>(columnSet.getString("COLUMN_NAME"),
                            InvertedTypes.toStr(columnSet.getString("DATA_TYPE"))));
                }
            }

            // creating tables
            for (String t : columnsMap.keySet()) {
                tables.put(t, new Table(connection, t, columnsMap.get(t)));
            }

            connected = true;
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println("No connection possible: Network is not available!");
            ErrorPopup.show("Connexion impossible", "Vérifiez l'état de votre connexion (VPN etc.)");
            Log.e(e.getMessage());
            return false;
        }
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public ArrayList<Table> getTables() {
        ArrayList<Table> rtn = new ArrayList<>();
        for (Table t : tables.values()) {
            rtn.add(t);
        }
        return rtn;
    }

    /**
     * Execute a SELECT query on the database 
     * @warning Not SQL injection safe
     * @param sql query
     * 
     * @todo TODO: Make a safe version
     */
    public ArrayList<Row> selectQuery(String sql) {
        ArrayList<Row> rtn = new ArrayList<>();
        Log.l(sql, 10);
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            if (rs == null)
                return rtn;

            ResultSetMetaData pmd = rs.getMetaData();
            Integer rowCount = 1 + pmd.getColumnCount();

            // res
            while (rs.next()) {
                Row r = new Row();
                
                for (int index = 1; index < rowCount; index++) {
                    r.add(pmd.getColumnName(index), rs.getObject(pmd.getColumnName(index)));
                    // Log.l(pmd.getColumnName(index) + ": " + rs.getObject(pmd.getColumnName(index)));
                }
                rtn.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * execute INSERT, UPDATE and DELETE queries on your database
     * @warning Not SQL injection safe
     * @param sql
     * @return number of lines modified | -1 in cas of error
     * 
     * @todo TODO: Make a safe version
     */
    public int modifyQuery(String sql) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String toString() {
        String rtn = "[DATABASE: " + this.database + "] (connected as " + this.user + ")\n\n";
        for (Table t : this.tables.values()) {
            rtn += t.toString() + "\n";
        }
        return rtn;
    }
}
