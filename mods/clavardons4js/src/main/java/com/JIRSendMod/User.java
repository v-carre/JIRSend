package com.JIRSendMod;

import com.JIRSendAPI.ModMessage;

import java.util.ArrayList;

public class User {

    // private static final Set<User> usernames = new HashSet<>();
    // The "usernames" class is a static Set of User, which guarantees that all usernames stored in this set are unique.
    // Nevertheless, an ArrayList of User is enough.



    private String username;
    private final String ipAddress;
    private boolean state;
    private boolean change;
    private final String logiciel;
    private TCPClient socket;
    private int timeToLive=10;
    private ArrayList<ModMessage> messages=new ArrayList<>();
    public User(String username, String ipAddress, boolean state, String logiciel){
        this.username=username;
        this.ipAddress=ipAddress;
        this.state=state; // true if active, false if passive
        this.change=false;
        this.logiciel=logiciel;
        this.socket=null;
    }
    public User(String username, String ipAddress, boolean state, boolean change, String logiciel){
        this.username=username;
        this.ipAddress=ipAddress;
        this.state=state; // true if active, false if passive
        this.change=change;
        this.logiciel=logiciel;
        this.socket=null;
    }
    public void addMessage(ModMessage message){
        this.messages.add(message);
    }
    public String getUsername(){
        return this.username;
    }
    public String getIpAddress(){
        return this.ipAddress;
    }
    public boolean getState(){return this.state;}
    public boolean getChange(){return this.change;}
    public String getLogiciel(){return this.logiciel;}
    public int getTimeToLive(){return this.timeToLive;}
    public TCPClient getSocket(){return this.socket;}
    public ArrayList<ModMessage> getMessages(){return this.messages;}

    public void setUsername(String username){
        this.username=username;
    }
    public void setState(boolean state){
        this.state=state;
        if(state)this.setTimeToLive();
    }
    public void setChange(boolean change){this.change=change;}
    public void setTimeToLive(){this.timeToLive=10;}
    public void setSocket(TCPClient socket){this.socket=socket;}


    public void decrementTTL(){this.timeToLive--;}

    public static void DisplayUsernames(ArrayList<User> arrayUsers){
        int i=0;
        for (User user : arrayUsers){
            System.out.println(user);
            i++;
        }
        System.out.println("Total d'utilisateurs: "+i);
    }


    @Override
    public String toString() {
        if(this.state)return String.format("Username: %s, Adresse IP: %s, Logiciel: %s, Statut: Online", this.username,this.ipAddress,this.logiciel);
        else return String.format("Username: %s, Adresse IP: %s, Logiciel: %s, Statut: Offline", this.username,this.ipAddress,this.logiciel);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof User)) return false;
        User o = (User) obj;
        return o.ipAddress.equals(this.ipAddress);
    }
}
