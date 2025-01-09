package com.JIRSendMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient extends Thread{
    private  Socket socket;
    private  UserList list;
    private Observer observer;
    private PrintWriter out;
    private BufferedReader in;
    private boolean stateThread;

    public interface Observer {
        void connectTCP(String ipAddr, TCPClient socket);
        void connectResponseTCP(String ipAddr, boolean response);
        void messageTCP(String username, String message);
        void quitTCP(String ipAddr);
    }
    public TCPClient(Socket socket) {
        this.socket = socket;
        this.list = UserList.getInstance();
        try {
            this.out = new PrintWriter(this.socket.getOutputStream(), true); // pour envoyer des messages
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); // pour lire des messages entrants
            this.observer=UserList.getInstance();
        }catch (IOException e){
            System.err.println("Critical error: could not create TCP Client in Clavardons: "+socket+"\n"+e);
        }
    }
    public TCPClient(String ipAddr) {
        try {
            this.socket = new Socket(ipAddr, 1643);
            this.list = UserList.getInstance();
            this.out = new PrintWriter(this.socket.getOutputStream(), true); // pour envoyer des messages
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); // pour lire des messages entrants
            this.observer=UserList.getInstance();
        }catch (IOException e){
            System.err.println("Critical error: could not create TCP Client in Clavardons: "+ipAddr+"\n"+e);
        }
        this.sendConnect();
    }

    @Override
    public void run() {
        try {
            String received;
            this.stateThread=true;
            while ((received = in.readLine()) != null&&(this.stateThread)) {
                String[] NameAndPayload = getNameAndPayload(received);
                String[] words = NameAndPayload[1].split(" ", 2);
                switch (words[0]) {
                    case "Connect":
                        observer.connectTCP(socket.getInetAddress().getHostAddress(),this);
                        break;
                    case "ConnectResponse":
                        if(!Boolean.parseBoolean(words[1]))socket.close();
                        observer.connectResponseTCP(socket.getInetAddress().getHostAddress(), Boolean.parseBoolean(words[1]));
                        break;
                    case "SendMessage":
                        observer.messageTCP(socket.getInetAddress().getHostAddress(), words[1]);
                        break;
                    case "Quit":
                        socket.close();
                        observer.quitTCP(socket.getInetAddress().getHostAddress());
                        break;
                }
            }
        } catch (IOException e) {
            //LOGGER.info("Thread stopped");
        }finally {
            try {
                System.err.println("Clavardons: Thread stopped");
                if(stateThread)socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String[] getNameAndPayload(String msg) {
        String[] ret = new String[2];
        String[] words = msg.split("-", 2);

        words = words[1].split(">", 2);
        ret[0] = words[0];

        words = words[1].split("\\|", 2);
        ret[1] = words[1];
        return ret;
    }
    public void stopThread() {
        this.sendQuit();
        this.stateThread = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        out.println("-Clavardons><|SendMessage "+message);
    }
    public void sendConnectResponse(boolean response){
        out.println("-Clavardons><|ConnectResponse "+response);
    }
    public void sendQuit(){
        out.println("-Clavardons><|Quit");
    }
    public void sendConnect(){
        out.println("-Clavardons><|Connect");
    }
}
