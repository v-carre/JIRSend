package com.JIRSend.model.db;

public class TestDatabase extends ACLVDatabase {
    private static int testNumber = 0;
    private boolean init = false;

    private synchronized static int getTestNumber() {
        return testNumber++;
    }

    /**
     * Create a local ACLV test Database
     */
    public TestDatabase() {
        super("testDatabase" + getTestNumber());
        this.init = false;
    }

    public boolean isInit() {
        return init;
    }

    public boolean connect() {
        boolean connectionStatus = super.connect();
        if (connectionStatus)
            this.init();
        return connectionStatus;
    }

    private void init() {
        this.modifyQuery(
                "create table user (id_user integer PRIMARY KEY AUTOINCREMENT, nom varchar(32) not null, prenom varchar(32) not null)");
        this.modifyQuery(
                "create table volontaire (id_volontaire integer PRIMARY KEY AUTOINCREMENT, nom varchar(32) not null, prenom varchar(32) not null)");
        this.modifyQuery(
                "create table admin (id_admin integer PRIMARY KEY AUTOINCREMENT, nom varchar(32) not null, prenom varchar(32) not null)");
        this.modifyQuery(
                "create table demande (id_demande integer PRIMARY KEY AUTOINCREMENT, texte varchar(8192) not null, date DATETIME default CURRENT_TIMESTAMP )");
        this.modifyQuery(
                "create table user_demande (id_user integer, id_demande integer, statut varchar(32), foreign key (id_user) references user(id_user) on delete cascade, foreign key (id_demande) references demande(id_demande) on delete cascade)");
        this.modifyQuery(
                "create table user_admin (id_user integer, id_admin integer, foreign key (id_user) references user(id_user) on delete cascade, foreign key (id_admin) references admin(id_admin) on delete cascade)");
        this.init = true;
    }
}
