package com.gestionProjet;

import com.gestionProjet.db.GenericDatabase;
import com.gestionProjet.db.Row;
import com.gestionProjet.ui.Log;
import com.gestionProjet.ui.MainWindow;

public class App {
    public static void main(String[] args) {
        Log.setVerbose(true);
        Log.l("Starting ACLV...");
        System.out.println( "Hello World!" );

        GenericDatabase db = new GenericDatabase("mysql://srv-bdens.insa-toulouse.fr:3306", "projet_gei_016", "projet_gei_016", "yoo4No8o");
        db.connect();
        System.out.println(db);
        for (Row r : db.selectQuery("Select * FROM demande")) {
            Log.l(r.toString());
        }
        new MainWindow().open();
    };

    public static int add(int a, int b) {
        return a + b;
    }
}
