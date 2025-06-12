package data.story;

import data.MySqlConnector;

/**
 * Handles story creation, retrieval, and listing by creator.
 */
public class StoryDAO {
    private MySqlConnector connector;

    public StoryDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
