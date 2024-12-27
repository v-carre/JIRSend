package com.JIRSend.model.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.JIRSend.view.cli.Log;

public class ACLVDatabase extends GenericDatabase {
    public static String UserTable = "user";
    public static String HelperTable = "volontaire";
    public static String AdminTable = "admin";

    // public ACLVDatabase(String serverAddress, String database, String user, String password) {
    //     super(serverAddress, database, user, password);
    // }

    // // Default database
    // /**
    //  * Create classic database connection
    //  */
    // public ACLVDatabase() {
    //     super("mysql://srv-bdens.insa-toulouse.fr:3306", "projet_gei_016", "projet_gei_016", "yoo4No8o");
    // }

    /**
     * Create a local test database
     * 
     * @param testDatabase
     */
    protected ACLVDatabase(String testDatabase) {
        super(testDatabase);
    }

    public void insertDemande(int userId, String content) {
        // temporarly turn off auto-commit for transactional operations
        try {
            connection.setAutoCommit(false);

            // STEP 1: Insert demande
            String insertDemandeSql = "INSERT INTO demande (texte) VALUES (?)";
            try (
                    PreparedStatement insertDemandeStmt = connection.prepareStatement(insertDemandeSql,
                            Statement.RETURN_GENERATED_KEYS)) {
                insertDemandeStmt.setString(1, content);
                insertDemandeStmt.executeUpdate();

                // retrieve the last inserted ID
                ResultSet rs = insertDemandeStmt.getGeneratedKeys();
                if (rs.next()) {
                    int lastDemandeId = rs.getInt(1);

                    // STEP 2: check user_admin entry
                    String checkUserAdminSql = "SELECT EXISTS (SELECT 1 FROM user_admin WHERE id_user = ?)";
                    try (PreparedStatement checkUserAdminStmt = connection.prepareStatement(checkUserAdminSql)) {
                        checkUserAdminStmt.setInt(1, userId);
                        ResultSet checkResult = checkUserAdminStmt.executeQuery();

                        String statut = "W"; // default statut is Waiting
                        if (checkResult.next() && checkResult.getBoolean(1)) {
                            statut = "NA"; // statut is Need Approbation if user and admin have a correspondance
                        }

                        // STEP 3: Insert into user_demande
                        String insertUserDemandeSql = "INSERT INTO user_demande (id_user, id_demande, statut) VALUES (?, ?, ?)";
                        try (PreparedStatement insertUserDemandeStmt = connection
                                .prepareStatement(insertUserDemandeSql)) {
                            insertUserDemandeStmt.setInt(1, userId);
                            insertUserDemandeStmt.setInt(2, lastDemandeId);
                            insertUserDemandeStmt.setString(3, statut);

                            insertUserDemandeStmt.executeUpdate();
                        }
                    }
                }
            }

            // Commit the transaction
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            Log.e("Error while inserting new demand");
            e.printStackTrace();

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                Log.e("ERROR!: Could not set autocommit back to auto: " + e1.getMessage());
                e1.printStackTrace();
            }
        }
    }
}
