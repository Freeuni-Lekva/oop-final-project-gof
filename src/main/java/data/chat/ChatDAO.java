package data.chat;

import data.MySqlConnector;

/**
 * Manages chat sessions between users and story contexts.
 */
public class ChatDAO {
    private MySqlConnector connector;

    public ChatDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
