package data.chat;

import data.MySqlConnector;
import model.Message;

import java.util.ArrayList;

/**
 * Handles all messages sent and received in a chat session.
 */
public class MessageDAO {
    private MySqlConnector connector;

    public MessageDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }

    public String getMessage(int message_id){
        return "";
    }

    public ArrayList<Message> getMessages(int chat_id){
        return null;
    }

}



