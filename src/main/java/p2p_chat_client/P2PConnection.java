package p2p_chat_client;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class P2PConnection implements Controller {

    private String myname;
    private Chat chat;

    public P2PConnection(String myname) {
        this.myname = myname;
    }

    public static P2PConnection start(String myname) {
        P2PConnection chat = new P2PConnection(myname);
        Server server = new Server(chat, myname);
        new Thread("Server") {
            public void run() {
                server.start();
            }
        }.start();
        return chat;

    }

    public static boolean isReachable(String IP) {
        try {
            return InetAddress.getByName(IP).isReachable(2000);
        } catch (Exception e) {
            return false;
        }
    }

    public void addClient(String IP) {
        System.out.println("Attempt to Connect to " + IP);
        //Ping not working always, thus not taking as base case
        new Thread("Client") {
            public void run() {
                Socket socket = connectTo(IP);
                if (socket == null) {

                    if (!isReachable(IP)) {
                        JOptionPane.showMessageDialog(null, "User Offline");
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "User Failed to Connect");
                } else {
                    addChat(socket, myname);
                }
            }
        }.start();

    }

    private static Socket connectTo(String IP) {
        InetAddress address;
        try {
            address = InetAddress.getByName(IP);
        } catch (UnknownHostException ex) {
            System.err.println("Invalid IP");
            return null;
        }
        try {
            return new Socket(address, Server.PORT);
        } catch (IOException ex) {
            System.err.println("Couldn't Connected");
            return null;
        }

    }

    public void addSystemMessage(String str) {
        if (chat != null) {
            chat.addSystemMessage(str);
        }
    }

    @Override
    public void addChat(Socket socket, String myname) {
        chat = new Chat(socket, myname);
        chat.start();
    }

}
