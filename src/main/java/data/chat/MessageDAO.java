package data.chat;

import data.MySqlConnector;

/**
 * Handles all messages sent and received in a chat session.
 */
public class MessageDAO {
    private MySqlConnector connector;

    public MessageDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
