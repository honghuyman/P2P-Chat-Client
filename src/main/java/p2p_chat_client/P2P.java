package p2p_chat_client;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class P2P {

    private String myname;
    private Chat chat;

    public P2P(String myname) {
        this.myname = myname;
    }

    public static P2P start(String myname) {
        P2P connection = new P2P(myname);
        ChatServer chatServer = new ChatServer(connection, myname);
        FileTransfer fileTransferServer = new FileTransfer();
        new Thread("Chat Server") {
            public void run() {
                chatServer.start();
            }
        }.start();
        new Thread("File Transfer Server") {
            public void run() {
                fileTransferServer.startServer();
            }
        }.start();
        return connection;

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
            return new Socket(address, ChatServer.PORT);
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

    public void addChat(Socket socket, String myname) {
        chat = new Chat(socket, myname);
        chat.start();
    }

}
