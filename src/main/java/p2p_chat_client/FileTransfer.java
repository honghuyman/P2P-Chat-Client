package p2p_chat_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileTransfer extends javax.swing.JFrame {

    public static int PORT = 9999;

    public boolean startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("File Server is running on port " + PORT);
            while (true) {
                Socket s = server.accept();
                new Thread(() -> {
                    try {
                        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                        while (true) {
                            FileData file = (FileData) in.readObject();
                            receiveFile(file);
                        }
                    } catch (Exception e) {
                    }
                }).start();
            }
        } catch (IOException e) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public void receiveFile(FileData data) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int option = JOptionPane.showConfirmDialog(null, data.getSender() + " sent you a file. Do you want to save?", "File Transfer", dialogButton);
        if (option == JOptionPane.YES_OPTION) {
            JFileChooser ch = new JFileChooser();
            ch.setSelectedFile(new File(data.getName()));
            int c = ch.showSaveDialog(null);
            if (c == JFileChooser.APPROVE_OPTION) {
                try {
                    try (FileOutputStream out = new FileOutputStream(ch.getSelectedFile())) {
                        out.write(data.getFile());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Could not save file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void sendFile(String serverIP, String myname) {
        try {
            Socket socket = new Socket(serverIP, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            JFileChooser ch = new JFileChooser();
            int c = ch.showOpenDialog(null);
            if (c == JFileChooser.APPROVE_OPTION) {
                ch.setVisible(true);
                File f = ch.getSelectedFile();
                FileInputStream in = new FileInputStream(f);
                byte b[] = new byte[in.available()];
                in.read(b);
                FileData data = new FileData();
                data.setName(f.getName());
                data.setFile(b);
                data.setSender(myname);
                out.writeObject(data);
                out.flush();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not send file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
