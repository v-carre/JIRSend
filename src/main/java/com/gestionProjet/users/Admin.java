package com.gestionProjet.users;

public class Admin extends BaseUser {
    public Admin(String nom,String prenom) {
        super(nom,prenom);
        if(!exists()) {
            this.id = createUser();
            System.out.println("User created: " + nom + " " + prenom + " " + id);
        } else
            this.id = 0; //get from db
    }

    protected boolean exists()
    {
        //return db.execute("select * from user where nom=nom and prenom=prenom").length() != 0;
        return false;
    }

    protected int createUser()
    {
        return 0;//from db
    }

    @Override
    public String getType() {
        return "Admin";
    }
}
