package data.story;

import data.MySqlConnector;

/**
 * Manages creation and retrieval of tags used to label stories.
 * Handles relationships between stories and their associated tags.
 */
public class TagsDAO {
    private MySqlConnector connector;

    public TagsDAO(MySqlConnector sqlConnector) {
        connector = sqlConnector;
    }
}
