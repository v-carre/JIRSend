package com.JIRSendMod;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class UDPServer extends Thread {
    private MulticastSocket socket;
    private volatile boolean state=true;
    private final UserList list;
    private final ArrayList<Observer> observers;

    public interface Observer {
        void updateUDPServer(String type, String[] args);
    }
    public void subscribe(Observer observer){
        this.observers.add(observer);
    }

    @SuppressWarnings("deprecation")
    public UDPServer(){
        super();
        this.list=UserList.getInstance();
        observers=new ArrayList<>();
        try{
            this.socket = new MulticastSocket(MyNetworkInterface.USER_PORT);
            this.socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
            this.socket.joinGroup(InetAddress.getByName(MyNetworkInterface.MULTICAST_ADDRESS)); //depracated mais beaucoup plus simple de cette manière
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void run(){
        try{
            listener();
        }catch (Exception e) {
            System.err.println("Critical error in Clavardons UDP Server: "+e);
        }
    }
    private void listener() throws IOException {
        byte[] bufIn = new byte[256];
        String[] args;
        while (state) {
            DatagramPacket inPacket  = new DatagramPacket(bufIn, bufIn.length);
            try{
                socket.receive(inPacket);
                String received = new String(inPacket.getData(), 0, inPacket.getLength());
                String[] NameAndPayload =this.getNameAndPayload(received);
                String[] words = NameAndPayload[1].split(" ",2);
                switch (words[0]){
                    case "GetUser":
                        User me = this.list.getMe();
                        if(me!=null){
                            byte[] bufOut = String.format("-Clavardons><|NewUser %s",me.getUsername()).getBytes();
                            DatagramPacket outPacket= new DatagramPacket(bufOut, bufOut.length, inPacket.getAddress(), MyNetworkInterface.USER_PORT);
                            socket.send(outPacket);
                        }
                        break;
                    case "NewUser":
                        args = new String[3];
                        args[0] = words[1];
                        args[1] = inPacket.getAddress().getHostAddress();
                        args[2] = NameAndPayload[0];
                        for(Observer observer:observers)observer.updateUDPServer("NewUser",args);
                        break;
                    case "SetUserOffline":
                        args = new String[1];
                        args[0] = words[1];
                        for(Observer observer:observers)observer.updateUDPServer("SetUserOffline",args);
                        break;
                    case "UpdateUsername":
                        args = new String[2];
                        args[0] = words[1];
                        args[1] = inPacket.getAddress().getHostAddress();
                        for(Observer observer:observers)observer.updateUDPServer("UpdateUsername",args);
                        break;
                    case "TimeToLive":
                        args = new String[1];
                        args[0] = inPacket.getAddress().getHostAddress();
                        for(Observer observer:observers)observer.updateUDPServer("TimeToLive",args);
                        break;
                }
            } catch (SocketException e){
                if(!state)System.out.println("<Clavardons-Arrêt> -> Arrêt du thread d'écoute UDP");
                else e.printStackTrace();
            }
        }
    }
    private String[] getNameAndPayload(String msg){
        String[] ret=new String[2];
        String[] words = msg.split("-", 2);

        words=words[1].split(">",2);
        ret[0]= words[0];

        words=words[1].split("\\|",2);
        ret[1]=words[1];
        return ret;
    }
    public void stopThread(){
        this.state=false;
        socket.close();
    }

}
