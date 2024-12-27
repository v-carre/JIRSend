package com.JIRSend.model.db;

import java.math.BigInteger;
import java.util.ArrayList;

import com.JIRSend.view.cli.Log;

public class CommonRequest {
    static private boolean addSomeone(ACLVDatabase db, String tableName, String nom, String prenom) {
        if (someoneExists(db, tableName, nom, prenom))
            return false;
        db.modifyQuery("insert into " + tableName + " (nom,prenom) values ('" + nom + "','" + prenom + "')");
        return true;
    }

    static public boolean addUser(ACLVDatabase db, String nom, String prenom) {
        return addSomeone(db, ACLVDatabase.UserTable, nom, prenom);
    }

    static public boolean addAdmin(ACLVDatabase db, String nom, String prenom) {
        return addSomeone(db, ACLVDatabase.AdminTable, nom, prenom);
    }

    static public boolean addHelper(ACLVDatabase db, String nom, String prenom) {
        return addSomeone(db, ACLVDatabase.HelperTable, nom, prenom);
    }

    static private boolean someoneExists(ACLVDatabase db, String tableName, String nom, String prenom) {
        ArrayList<Row> result = db
                .selectQuery("select nom from " + tableName + " where nom='" + nom + "' and prenom='" + prenom + "'");
        return result.size() > 0;
    }

    static public boolean userExists(ACLVDatabase db, String nom, String prenom) {
        return someoneExists(db, ACLVDatabase.UserTable, nom, prenom);
    }

    static public boolean adminExists(ACLVDatabase db, String nom, String prenom) {
        return someoneExists(db, ACLVDatabase.AdminTable, nom, prenom);
    }

    static public boolean helperExists(ACLVDatabase db, String nom, String prenom) {
        return someoneExists(db, ACLVDatabase.HelperTable, nom, prenom);
    }

    static public int intFromObject(Object o) {
        if (o == null)
            Log.e("Got null");
        if (o == null)
            return -1;
        // System.err.println(o.getClass());
        if (o instanceof Integer)
            return ((Integer) o).intValue();
        if (o instanceof BigInteger)
            return ((BigInteger) o).intValue();
        Log.e("Unknown object: " + o + " " + o.getClass());
        return -1;
    }

    static private int getSomeoneID(ACLVDatabase db, String tableName, String nom, String prenom) {
        ArrayList<Row> result = db.selectQuery("select id_" + tableName + " from " + tableName + " where nom='" + nom
                + "' and prenom='" + prenom + "'");
        if (result.size() == 0)
            return -1;
        if (result.size() != 1) {
            Log.e("Multiple results in getID: " + result);
            return -1;
        }
        Row row = result.get(0);
        Object id = row.getValue("id_" + tableName);
        return intFromObject(id);
    }

    /**
     * @param db
     * @param nom
     * @param prenom
     * @return -1 if user does not exists, user_id otherwise
     */
    static public int getUserID(ACLVDatabase db, String nom, String prenom) {
        return getSomeoneID(db, ACLVDatabase.UserTable, nom, prenom);
    }

    static public int getAdminID(ACLVDatabase db, String nom, String prenom) {
        return getSomeoneID(db, ACLVDatabase.AdminTable, nom, prenom);
    }

    static public int getHelperID(ACLVDatabase db, String nom, String prenom) {
        return getSomeoneID(db, ACLVDatabase.HelperTable, nom, prenom);
    }

    // static public ArrayList<UserRecord> getLinkedUsersFromAdmin(ACLVDatabase db, String nomAdmin, String prenomAdmin) {
    //     ArrayList<Row> result = db.selectQuery(
    //             "select u.nom,u.prenom,u.id_user from (user_admin ua join admin a on ua.id_admin=a.id_admin) join user u on u.id_user=ua.id_user where a.nom='"
    //                     + nomAdmin + "' and a.prenom='" + prenomAdmin + "'");
    //     ArrayList<UserRecord> ret = new ArrayList<>(result.size());
    //     for (Row row : result)
    //         ret.add(new UserRecord((String) row.getValue("nom"), (String) row.getValue("prenom"),
    //                 intFromObject(row.getValue("id_user")), UserType.User));
    //     return ret;
    // }

    // /**
    //  * @param db
    //  * @param id_admin
    //  * @param id_user
    //  * @return true if successfully inserted, false if link already exists
    //  */
    // static public boolean linkAdminToUser(ACLVDatabase db, int id_admin, int id_user) {
    //     ArrayList<Row> shouldBeEmpty = db
    //             .selectQuery("select * from user_admin where id_user=" + id_user + " and id_admin=" + id_admin);
    //     if (shouldBeEmpty.size() != 0)
    //         return false;
    //     db.modifyQuery("insert into user_admin (id_user,id_admin) values (" + id_user + "," + id_admin + ")");
    //     return true;
    // }

    // static public void updateTaskState(ACLVDatabase db, Task task) {
    //     int nb = db.modifyQuery("update user_demande set statut='" + Task.TaskState2String(task.state)
    //             + "' where id_demande=" + task.id);
    //     if (nb != 1) {
    //         Log.e("Error: number of row modified: " + nb + " in updateTaskState(" + task + ")");
    //     }
    // }

    // static public ArrayList<Task> getAllTasks(ACLVDatabase db) {
    //     ArrayList<Row> res = db.selectQuery(
    //             "select u.id_user as id_user, u.nom as nom,u.prenom as prenom,ud.id_demande as id_demande,d.texte as texte,ud.statut from (("
    //                     + ACLVDatabase.UserTable
    //                     + " u join user_demande ud on u.id_user=ud.id_user) join demande d on d.id_demande=ud.id_demande)");
    //     ArrayList<Task> tasks = new ArrayList<>(res.size());
    //     for (Row row : res)
    //         tasks.add(new Task(intFromObject(row.getValue("id_demande")),
    //                 intFromObject(row.getValue("id_user")),
    //                 (String) row.getValue("nom"),
    //                 (String) row.getValue("prenom"),
    //                 (String) row.getValue("texte"),
    //                 Task.String2TaskState((String) row.getValue("statut"))));
    //     return tasks;
    // }

    // static public void createTask(ACLVDatabase db, Task task) {
    //     db.insertDemande(task.id_user, task.content);
    // }

    static public boolean isAdminLinkedToUser(ACLVDatabase db, int id_admin, int id_user) {
        return db.selectQuery("select id_user from user_admin where id_user=" + id_user + " and id_admin=" + id_admin)
                .size() == 1;
    }
}
