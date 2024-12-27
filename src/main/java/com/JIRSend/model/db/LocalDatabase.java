package com.JIRSend.model.db;

// import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.JIRSend.view.cli.Log;

public class LocalDatabase {
    private boolean connected;
    private String database;
    private String user;

    protected Connection connection;

    /**
     * Create a specific database
     */
    public LocalDatabase(String localDatabaseName) {
        this.connected = false;
        this.database = localDatabaseName;
        Log.l("Created local database " + database, Log.LOG);
    }

    /**
     * Create database
     */
    public LocalDatabase() {
        this.connected = false;
        this.database = "history";
        Log.l("Created local database " + this.database, Log.LOG);
    }

    public boolean isConnected() {
        return connected;
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
            // if (!new File("./" + database + ".db").exists()) {
            // this.connection = DriverManager.getConnection("jdbc:sqlite:" + database +
            // ".db");
            // } else
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + database + ".db");

            if (connection == null) {
                Log.e("Connection failed");
                return false;
            }

            Log.l("Connected.", Log.LOG);

            connected = true;
            return true;
        } catch (Exception e) {
            Log.e("Unable to create local database. Check your permissions!");
            Log.e(e.getMessage());
            return false;
        }
    }

    /**
     * Execute a SELECT query on the database
     * 
     * @param sql          query
     * @param placeholders the replacements for ? in sql query
     * @return row corresponding to current selection
     * 
     */
    public ArrayList<Row> selectQuery(String query, ArrayList<Object> placeholders) {
        ArrayList<Row> rtn = new ArrayList<>();
        if (getOccurenceInString("\\?", query) != placeholders.size()) {
            Log.e("QUERY [" + getOccurenceInString("\\?", query)
                    + "] doesn't have the same amount of '?' than PLACEHOLDERS size [" + placeholders.size() + "]");
            return rtn;
        }
        // Log.l(query, Log.DEBUG);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int position = 0;
            for (Object object : placeholders) {
                preparedStatement.setObject(position, object);
                position++;
            }
            ResultSet rs = preparedStatement.executeQuery();

            if (rs == null)
                return rtn;

            ResultSetMetaData pmd = rs.getMetaData();
            Integer rowCount = 1 + pmd.getColumnCount();

            // res
            while (rs.next()) {
                Row r = new Row();

                for (int index = 1; index < rowCount; index++) {
                    r.add(pmd.getColumnName(index), rs.getObject(pmd.getColumnName(index)));
                }
                rtn.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * Execute a SELECT query on the database
     * 
     * @deprecated
     * @warning Not SQL injection safe
     * @param sql query
     * 
     */
    public ArrayList<Row> selectQuery(String sql) {
        ArrayList<Row> rtn = new ArrayList<>();
        Log.l(sql, Log.DEBUG);
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
                    // Log.l(pmd.getColumnName(index) + ": " +
                    // rs.getObject(pmd.getColumnName(index)));
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
     * 
     * @param sql          query
     * @param placeholders the replacements for ? in sql query
     * @return number of lines modified | -1 in case of error
     * 
     */
    public int modifyQuery(String query, ArrayList<Object> placeholders) {
        if (getOccurenceInString("\\?", query) != placeholders.size()) {
            Log.e("QUERY [" + getOccurenceInString("\\?", query)
                    + "] doesn't have the same amount of '?' than PLACEHOLDERS size [" + placeholders.size() + "]");
            return -1;
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            int position = 0;
            for (Object object : placeholders) {
                preparedStatement.setObject(position, object);
                position++;
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * execute INSERT, UPDATE and DELETE queries on your database
     * 
     * @deprecated
     * @warning Not SQL injection safe
     * @param sql
     * @return number of lines modified | -1 in case of error
     * 
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
        String rtn = "[DATABASE: " + this.database + "] (connected as " + this.user + ")";
        return rtn;
    }

    private int getOccurenceInString(String regex, String string) {
        if (string == null || string.isEmpty())
            return 0;
        return string.split(regex).length - 1;
    }
}
