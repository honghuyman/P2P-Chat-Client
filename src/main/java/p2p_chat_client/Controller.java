package p2p_chat_client;

import java.net.Socket;

public interface Controller {

    void addChat(Socket socket, String myname);
}
