package com.JIRSendMod;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class MyNetworkInterface {

    private static final int SERVER_PORT = 1553;
    static final int USER_PORT = 1610;
    static final String MULTICAST_ADDRESS = "233.009.007.034";
    private final MulticastSocket socket;
    private UDPServer threadUDP;
    private TCPServer threadTCP;
    public MyNetworkInterface() throws IOException{
        this.socket= new MulticastSocket(SERVER_PORT);
        this.socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
    }

    public void NetworkInterfaceDel(User sender) throws IOException{
        byte[] bufOut = String.format("-Clavardons><|SetUserOffline %s",sender.getUsername()).getBytes();
        DatagramPacket outPacket= new DatagramPacket(bufOut, bufOut.length,InetAddress.getByName(MULTICAST_ADDRESS), USER_PORT);
        socket.send(outPacket);
        socket.close();
    }


    public void getAllUser() throws IOException{
        byte[] bufOut= "-Clavardons><|GetUser".getBytes();
        DatagramPacket outPacket= new DatagramPacket(bufOut, bufOut.length,InetAddress.getByName(MULTICAST_ADDRESS), USER_PORT);
        socket.send(outPacket);
    }

    public void sendNewUser(User sender) throws IOException{
        byte[] buf= String.format("-Clavardons><|NewUser %s",sender.getUsername()).getBytes();
        DatagramPacket outPacket= new DatagramPacket(buf, buf.length,InetAddress.getByName(MULTICAST_ADDRESS), USER_PORT);
        socket.send(outPacket);
    }

    public void sendUpdateUsername(String newUsername){
        try {
            byte[] buf = String.format("-Clavardons><|UpdateUsername %s", newUsername).getBytes();
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS), USER_PORT);
            socket.send(outPacket);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageUDP(User sender, User receiver, String msg){
        try {
            byte[] buf = String.format("-Clavardons><|SendMessage %s:%s", sender.getUsername(), msg).getBytes();
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(receiver.getIpAddress()), USER_PORT);
            socket.send(outPacket);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendTimeToLive(){
        try {
            byte[] buf = "-Clavardons><|TimeToLive".getBytes();
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS), USER_PORT);
            socket.send(outPacket);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String getIpAddr() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Ignorer les interfaces non actives et les boucles locales
                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // Vérifie si l'adresse est de type IPv4
                    if (address instanceof java.net.Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Erreur lors de la récupération des adresses IP.");
            e.printStackTrace();
        }
        return "Not found";
    }

    public void startUDPListeningThread(){
        this.threadUDP= new UDPServer();
        this.threadUDP.start();
    }

    public void subscribeOnUDPServer(UDPServer.Observer observer){
        this.threadUDP.subscribe(observer);
    }

    public void stopUDPListeningThread(){
        this.threadUDP.stopThread();
    }

    public void startTCPListeningThread(){
        this.threadTCP= new TCPServer();
        this.threadTCP.start();
    }


    public void stopTCPListeningThread(){
        this.threadTCP.stopThread();
    }

}
