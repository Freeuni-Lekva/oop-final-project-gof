package model.chat;

import java.util.ArrayList;
import java.util.Collections;

public class ChatHistory {

    private ArrayList<Message> chatHistory;

    public ChatHistory(ArrayList<Message> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public String generateChat() {
        if (chatHistory.isEmpty()) return "";

        Collections.sort(chatHistory);
        StringBuilder sb = new StringBuilder();

        for (Message message : chatHistory) {

            if (message.isPrompt()) {
                sb.append("Information about the world : \n");
                sb.append(message.getMessage()).append("\n");
                continue;
            }

            if (message.isUser()) {
                sb.append("User : ");
                sb.append(message.getMessage()).append("\n");
                continue;
            }

            sb.append("Narrator :");
            sb.append(message.getMessage()).append("\n");

        }

        return sb.toString();
    }

}