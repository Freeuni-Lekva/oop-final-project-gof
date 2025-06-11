package data.media;

import data.MySqlConnector;

/**
 * Manages post creation within a story, including image handling.
 * */
public class PostDAO {
    private MySqlConnector connector;

    public PostDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
