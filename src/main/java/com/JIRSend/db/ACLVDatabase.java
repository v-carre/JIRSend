package com.JIRSend.db;

public class ACLVDatabase extends GenericDatabase {
    public ACLVDatabase(String serverAddress, String database, String user, String password) {
        super(serverAddress, database, user, password);
    }

    // Default database
    public ACLVDatabase() {
        super("mysql://srv-bdens.insa-toulouse.fr:3306", "projet_gei_016", "projet_gei_016", "yoo4No8o");
    }

    // TODO: Add method that execute ACLV's specific requests
}
