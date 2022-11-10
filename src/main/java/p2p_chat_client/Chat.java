package p2p_chat_client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chat implements ChatController {

    private Socket socket;
    private UI ui;
    private BufferedReader br;
    private BufferedOutputStream bos;
    private String myname, othername;

    public Chat(Socket socket, String myname) {
        this.socket = socket;
        this.myname = myname;
    }

    @Override
    public void sendMessage(String msg) {
        ui.addMessage(msg, myname);
        try {
            bos.write((msg + "\n").getBytes());
            bos.flush();
        } catch (IOException ex) {
            ui.addMessage("Failed to send above message", "System");
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUI(UI ui) {
        this.ui = ui;
        System.out.println("UI is init [" + (ui != null) + "]");
    }

    public boolean start() {
        System.out.println("Starting Chat for " + socket);

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bos = new BufferedOutputStream(socket.getOutputStream());
            bos.write((myname + "\n").getBytes());
            bos.flush();
            othername = br.readLine();
            UI.launch(this, myname, othername);
            while (ui == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            new Reader().start();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void startFileTransferClient() {
        String ip = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString().substring(1);
        FileTransferClient.startWindow(ip, myname);
    }

    class Reader extends Thread {

        @Override
        public void run() {
            System.out.println("Reader Started");
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    ui.addMessage(line, othername);
                }

            } catch (IOException ex) {
                addSystemMessage("Chat Ended!");
            }

        }

    }

    public void addSystemMessage(String msg) {
        ui.addMessage(msg, "System");
    }

}
