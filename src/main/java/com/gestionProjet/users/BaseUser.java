package com.gestionProjet.users;

import com.gestionProjet.db.ACLVDatabase;
import com.gestionProjet.db.Row;

import java.util.ArrayList;

public abstract class BaseUser {
    protected String nom;
    protected String prenom;
    protected int id;
    protected ACLVDatabase db;
    public BaseUser(String nom,String prenom) {
        this.nom = nom;
        this.prenom = prenom;
        db = new ACLVDatabase();
        if(!db.connect())
            System.err.println("Could not connect to database. Maybe consider using INSA's VPN ?");
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    protected void setNom(String nom) {
        this.nom = nom;
    }
    protected void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    protected void setId(int id) {
        this.id = id;
    }

    public abstract String getType();// { return "Base User"; }

    protected void createUser(String table) {
        int result = db.modifyQuery("insert into " + table + " (nom,prenom) values ('" + nom + "','" + prenom + "')");
        if (result != 1)
            System.err.println("User created: " + result);
    }

    public int getId(String table) {
        ArrayList<Row> res = db.selectQuery("select * from " + table + "where nom='" + nom + "' and prenom='" + prenom + "'");
        if(res.size() != 1) {
            System.err.println("Multiple user correspond to selection... " + res);
            return 0;
        }
        Object value = res.get(0).getValue("id_user");
        if(value instanceof Integer)
            return (int) value;
        System.err.println("Wrong type... " + value.getClass());
        return 0;
    }

    //protected abstract boolean exists();
}
