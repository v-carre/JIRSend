package com.gestionProjet.users;

public class User extends BaseUser {

    public User(String nom,String prenom) {
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

    protected int createUser() {
        super.createUser("user");
        return 0;
    }

    public int getId()
    {
        return super.getId("user");
    }

    @Override
    public String getType() {
        return "User";
    }
}
