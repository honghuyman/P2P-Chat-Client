package p2p_chat_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

    public static int PORT = 8989;
    private ServerSocket ssocket;
    private P2P controller;
    private String myname;

    public ChatServer(P2P controller, String myname) {
        this.controller = controller;
        this.myname = myname;
    }

    public boolean start() {
        try {
            ssocket = new ServerSocket(PORT);
            System.out.println("Chat Server is running on port " + PORT);
            while (true) {
                Socket socket = ssocket.accept();
                System.out.println("Client Connected from " + socket.getRemoteSocketAddress());
                new Thread(() -> controller.addChat(socket, myname)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
