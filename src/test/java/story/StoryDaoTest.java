package story;

import data.MySqlConnector;
import data.story.StoryDAO;
import junit.framework.TestCase;
import model.story.Story;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoryDaoTest extends TestCase {

    private Connection conn;
    private StoryDAO storyDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();
        storyDao = new StoryDAO();
    }

    public void testCreateAndGetStory() {
        storyDao.createStory("New Story", "Prompt for New Story", 1);
        Story retrievedStory = storyDao.getStory(3);

        assertNotNull(retrievedStory);
        assertEquals(3, retrievedStory.getStoryId());
        assertEquals(1, retrievedStory.getCreatorId());
        assertEquals("New Story", retrievedStory.getTitle());
        assertEquals("Prompt for New Story", retrievedStory.getPrompt());
    }

    public void testGetPrompt() {
        createInitialStories();

        String prompt = storyDao.getPrompt(4);
        assertEquals("Prompt for story 2.", prompt);

        String nonExistentPrompt = storyDao.getPrompt(99);
        assertNull(nonExistentPrompt);
    }

    public void testGetStoriesList() {
        createInitialStories();

        List<Story> storiesForCreator1 = storyDao.getStoriesList(1);

        assertNotNull(storiesForCreator1);
        assertEquals(3, storiesForCreator1.size());
        assertEquals("Third Story", storiesForCreator1.get(2).getTitle());

        List<Story> storiesForCreator99 = storyDao.getStoriesList(99);
        assertNotNull(storiesForCreator99);
        assertTrue(storiesForCreator99.isEmpty());
    }

    public void testGetStoriesByIds() {
        createInitialStories();

        List<Integer> idsToFetch = Arrays.asList(1, 3, 5);
        List<Story> foundStories = storyDao.getStoriesByIds(idsToFetch);

        assertEquals(3, foundStories.size());
        assertTrue(foundStories.stream().anyMatch(s -> s.getStoryId() == 1));
        assertTrue(foundStories.stream().anyMatch(s -> s.getStoryId() == 5));

        List<Story> emptyResult = storyDao.getStoriesByIds(new ArrayList<>());
        assertTrue(emptyResult.isEmpty());

        List<Story> nonExistentResult = storyDao.getStoriesByIds(Arrays.asList(98, 99));
        assertTrue(nonExistentResult.isEmpty());
    }

    public void testUpdateStory() {
        storyDao.createStory("Old Title", "Old prompt", 1);
        Story storyToUpdate = storyDao.getStory(3);
        assertNotNull(storyToUpdate);

        storyToUpdate.setTitle("New Updated Title");
        storyToUpdate.setPrompt("New updated prompt");
        storyDao.updateStory(storyToUpdate);

        Story updatedStory = storyDao.getStory(3);
        assertEquals("New Updated Title", updatedStory.getTitle());
        assertEquals("New updated prompt", updatedStory.getPrompt());
    }

    public void testDeleteStory() {
        createInitialStories();
        assertNotNull(storyDao.getStory(2));

        storyDao.deleteStory(2);

        Story deletedStory = storyDao.getStory(2);
        assertNull(deletedStory);
    }

    public void testFindBookmarkedStories() throws SQLException {
        createInitialStories();
        createInitialBookmarks();

        List<Story> bookmarked = storyDao.findBookmarkedStories(1);

        assertEquals(2, bookmarked.size());
        assertEquals("First Story", bookmarked.get(0).getTitle());
        assertEquals("Third Story", bookmarked.get(1).getTitle());

        List<Story> noBookmarks = storyDao.findBookmarkedStories(99);
        assertTrue(noBookmarks.isEmpty());
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

    // --- Helper methods ---

    private void createInitialStories() {
        storyDao.createStory("First Story", "Prompt for story 1.", 1);
        storyDao.createStory("Second Story", "Prompt for story 2.", 2);
        storyDao.createStory("Third Story", "Prompt for story 3.", 1);
    }

    private void createInitialBookmarks() throws SQLException {
        String sql = "INSERT INTO bookmarks (user_id, story_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 3);
            preparedStatement.addBatch();

            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 5);
            preparedStatement.addBatch();

            preparedStatement.setInt(1, 2);
            preparedStatement.setInt(2, 4);
            preparedStatement.addBatch();

            preparedStatement.executeBatch();
        }
    }
}