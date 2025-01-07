package party.loveto.chatsystem.network;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import party.loveto.chatsystem.cli.Colors;
import party.loveto.chatsystem.model.User;

public class TcpClient {

    private static final Logger LOGGER = LogManager.getLogger(TcpClient.class);

    private Socket clientSocket;
    public static PrintWriter out;
    public static BufferedReader in;
    private ClientHandler clientHandler;

    public TcpClient() {}

    static void writeDoubleToSocket(Socket socket, double number) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeDouble(number);
    }

    static double readDoubleFromSocket(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readDouble();
    }

    public void startConnection(User user, int port) throws IOException {
        startConnection(user.getIp().getHostName(), port);
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        ClientHandler clientHandler = new ClientHandler(clientSocket, out, in);
        this.clientHandler = clientHandler;
        clientHandler.start();
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public String sendMessageAcked(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }

    public void sendMessage(String msg) throws IOException, InterruptedException {
        if (msg == null || msg.isBlank()) {
            Colors.errorPrinting("Empty message, skipping...");
        } else {
            out.println(msg);
        }
    }

    /**
     * Listener class for use with DiscussionManager by model
     */
    public static interface DiscussionListener {
        /**
         * Handle incoming message. Should be cast to specific message class.
         * @param message
         */
        void handle(String message);
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
            Colors.printSuccessfulCommandOutput(
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
                Colors.errorPrinting("Error closing input stream: " + e.getMessage());
            }
        }

        public void sendMessage(String msg) throws IOException, InterruptedException {
            if (msg == null || msg.isBlank()) {
                Colors.errorPrinting("Empty message, skipping...");
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
}
