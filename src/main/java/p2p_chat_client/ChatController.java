package p2p_chat_client;

public interface ChatController {

    void sendMessage(String msg);

    void setUI(UI ui);

    void startFileTransferClient();
}
