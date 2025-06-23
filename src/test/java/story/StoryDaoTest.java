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

    public void testCreateAndGetStory() throws SQLException {
        storyDao.createStory("New Story", "Prompt for New Story", 1);
        Story retrievedStory = storyDao.getStory(3);

        assertNotNull(retrievedStory);
        assertEquals(3, retrievedStory.getStoryId());
        assertEquals(1, retrievedStory.getCreatorId());
        assertEquals("New Story", retrievedStory.getTitle());
        assertEquals("Prompt for New Story", retrievedStory.getPrompt());
    }

    public void testGetPrompt() throws SQLException {
        createInitialStories();

        String prompt = storyDao.getPrompt(4);
        assertEquals("Prompt for story 2.", prompt);

        String nonExistentPrompt = storyDao.getPrompt(99);
        assertNull(nonExistentPrompt);
    }

    public void testGetStoriesList() throws SQLException {
        createInitialStories();

        List<Story> storiesForCreator1 = storyDao.getStoriesList(1);

        assertNotNull(storiesForCreator1);
        assertEquals(3, storiesForCreator1.size());
        assertEquals("Third Story", storiesForCreator1.get(2).getTitle());

        List<Story> storiesForCreator99 = storyDao.getStoriesList(99);
        assertNotNull(storiesForCreator99);
        assertTrue(storiesForCreator99.isEmpty());
    }

    public void testGetStoriesByIds() throws SQLException {
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

    public void testUpdateStory() throws SQLException {
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

    public void testDeleteStory() throws SQLException {
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

    public void testAddBookmark() throws SQLException {
        List<Story> initialBookmarks = storyDao.findBookmarkedStories(1);
        assertTrue("User 1 should have no bookmarks initially.", initialBookmarks.isEmpty());

        storyDao.addBookmark(1, 2);

        List<Story> finalBookmarks = storyDao.findBookmarkedStories(1);
        assertEquals("User 1 should have 1 bookmark after adding one.", 1, finalBookmarks.size());
        assertEquals("The bookmarked story should be the correct one.", 2, finalBookmarks.get(0).getStoryId());
    }

    public void testRemoveBookmark() throws SQLException {
        createInitialStories();
        createInitialBookmarks();
        assertEquals("User 1 should have 2 bookmarks initially.", 2, storyDao.findBookmarkedStories(1).size());

        storyDao.removeBookmark(1, 5);

        List<Story> finalBookmarks = storyDao.findBookmarkedStories(1);
        assertEquals("User 1 should have 1 bookmark after removing one.", 1, finalBookmarks.size());
        assertEquals("The remaining bookmark should be for story 3.", 3, finalBookmarks.get(0).getStoryId());
    }

    public void testFindReadHistory() throws SQLException {
        createInitialStories();
        createInitialReadHistory();

        List<Story> history = storyDao.findReadHistory(1);

        assertNotNull("History list should not be null.", history);
        assertEquals("User 1 should have 2 stories in their history.", 2, history.size());
        assertTrue("History should contain 'Second Story'", history.stream().anyMatch(s -> s.getTitle().equals("Second Story")));
        assertTrue("History should contain 'Third Story'", history.stream().anyMatch(s -> s.getTitle().equals("Third Story")));
    }

    public void testAddReadHistory() throws SQLException {
        assertTrue("User 2 should have no history initially.", storyDao.findReadHistory(2).isEmpty());

        storyDao.addReadHistory(2, 1);

        List<Story> history = storyDao.findReadHistory(2);
        assertEquals("User 2 should have 1 item in history.", 1, history.size());
        assertEquals("The story in history should be story 1.", 1, history.get(0).getStoryId());

        storyDao.addReadHistory(2, 1);

        List<Story> historyAfterReRead = storyDao.findReadHistory(2);
        assertEquals("History count should remain 1 after re-Read.", 1, historyAfterReRead.size());
    }

    public void testRemoveReadHistory() throws SQLException {
        createInitialStories();
        createInitialReadHistory();
        assertEquals("User 1 should have 2 stories in history initially.", 2, storyDao.findReadHistory(1).size());

        storyDao.removeReadHistory(1, 4); // Corresponds to "Second Story"

        List<Story> finalHistory = storyDao.findReadHistory(1);
        assertEquals("User 1 should have 1 story in history after removing one.", 1, finalHistory.size());
        assertEquals("The remaining story should be story 5.", 5, finalHistory.get(0).getStoryId());
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

    // --- Helper methods ---

    private void createInitialStories() throws SQLException {
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
    
    private void createInitialReadHistory() throws SQLException {
        String sql = "INSERT INTO read_history (user_id, story_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 4); // "Second Story"
            preparedStatement.addBatch();

            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 5); // "Third Story"
            preparedStatement.addBatch();

            preparedStatement.setInt(1, 2);
            preparedStatement.setInt(2, 3); // "First Story"
            preparedStatement.addBatch();

            preparedStatement.executeBatch();
        }
    }
}