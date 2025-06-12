package data.media;

import data.MySqlConnector;

/**
 * Manages likes on posts and comments.
 */
public class LikesDAO {
    private MySqlConnector connector;

    public LikesDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
