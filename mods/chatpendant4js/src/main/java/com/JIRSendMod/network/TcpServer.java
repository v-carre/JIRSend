package com.JIRSendMod.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TcpServer extends Thread {

    public static int PORT;

    private ServerSocket serverSocket;
    private List<ConnexionListener> onConnexionOpened;

    public TcpServer(int port) {
        TcpServer.PORT = port;
        onConnexionOpened = new ArrayList<ConnexionListener>();
    }

    static void writeDoubleToSocket(Socket socket, double number) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeDouble(number);
    }

    static double readDoubleFromSocket(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readDouble();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
                );
                // New thread to listen messages
                ClientHandler clientHandler = new ClientHandler(clientSocket, out, in);

                clientHandler.start();
                // short time for race condition
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                synchronized (onConnexionOpened) {
                    for (ConnexionListener listener : onConnexionOpened) listener.handle(
                        clientHandler
                    );
                }
            }
        } catch (IOException e) {
        }
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

    public static class ClientHandler extends Thread {

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private List<DiscussionListener> messageReceived;

        public ClientHandler(
            Socket socket,
            PrintWriter printWriter,
            BufferedReader bufferedReader
        ) throws IOException {
            this.clientSocket = socket;
            this.out = printWriter;
            this.in = bufferedReader;
            this.messageReceived = new LinkedList<DiscussionListener>();
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        @Override
        public void run() {
            System.out.println(
                "A connexion has been established with " +
                clientSocket.getInetAddress().getHostName()
            );

            try {
                System.out.println("Message received");
                String message = "";
                while (!"End".equals(message)) {
                    message = in.readLine();
                    // notify listeners
                    synchronized (messageReceived) {
                        for (DiscussionListener listener : messageReceived) listener.handle(
                            message
                        );
                    }
                }
            } catch (IOException e) {
                System.out.println("Error closing input stream: " + e.getMessage());
            }
        }

        public void sendMessage(String msg) throws IOException, InterruptedException {
            if (msg == null || msg.isBlank()) {
                System.out.println("Empty message, skipping...");
            } else {
                out.println(msg);
            }
        }

        /**
         * Allows to add an observer that listens to when a discussion is opened on network.
         * @param listener
         */
        public void onMessageReceived(DiscussionListener listener) {
            System.out.println("Calling onDiscussionOpened");
            synchronized (messageReceived) {
                messageReceived.add(listener);
            }
        }
    }

    /**
     * Listener class for use with DiscussionManager by model
     */
    public interface DiscussionListener {
        /**
         * Handle incoming message. Should be cast to specific message class.
         * @param message
         */
        void handle(String message);
    }

    /**
     * Listener class for use with DiscussionManager by model
     */
    public interface ConnexionListener {
        /**
         * Handle incoming message. Should be cast to specific message class.
         * @param message
         */
        void handle(ClientHandler clientHandler);
    }

    /**
     * Allows to add an observer that listens to when a discussion is opened on network.
     * @param listener
     */
    public void onConnexionOpened(ConnexionListener listener) {
        System.out.println("Calling onDiscussionOpened");
        synchronized (onConnexionOpened) {
            onConnexionOpened.add(listener);
        }
    }
}
