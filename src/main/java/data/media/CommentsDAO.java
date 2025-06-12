package data.media;

import data.MySqlConnector;

/**
 * Handles comment operations on posts, including likes.
 */
public class CommentsDAO {
    private MySqlConnector connector;

    public CommentsDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
