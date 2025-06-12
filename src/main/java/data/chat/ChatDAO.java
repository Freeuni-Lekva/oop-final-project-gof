package data.chat;

import data.MySqlConnector;
import model.Message;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Manages chat sessions between users and story contexts.
 */
public class ChatDAO {
    private MySqlConnector connector;

    public ChatDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }

    public int getUserId(int chat_id) {
        Connection conn = connector.getConnection();
        connector.close(conn);
        return 0;
    }

    public String getStoryId(int chat_id) {
        Connection conn = connector.getConnection();
        connector.close(conn);
        return "";
    }

    public String getChatId(int user_id, int story_id){
        Connection conn = connector.getConnection();
        connector.close(conn);
        return "";
    }

    public int messageCount(int chat_id){
        return 0;
    }

}

