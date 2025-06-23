package story;

import data.MySqlConnector;
import data.story.StoryDAO;
import data.story.TagsDAO;
import junit.framework.TestCase;
import model.story.Story;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagsTest extends TestCase {
    private Connection conn;
    private TagsDAO tagsDAO;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

       tagsDAO = new TagsDAO();
    }

    public void testGetTags() throws SQLException {
        List<String> tags = tagsDAO.getStoryTags(1);
        assertEquals(2, tags.size());
        assertEquals("Adventure",tags.get(0));
        assertEquals("Mystery",tags.get(1));

        List<String> tags2 = tagsDAO.getStoryTags(2);
        assertEquals(2, tags2.size());
        assertEquals("Technology",tags2.get(1));
    }

    public void testGetStories() throws SQLException {
        List<String> tags = new ArrayList<>();
        tags.add("Adventure");
        tags.add("mystery");

        List<Integer> storyIds = tagsDAO.findStoryIdsByMultipleTags(tags);
        assertEquals(1, storyIds.size());
        assertEquals((Integer)1, storyIds.get(0));
    }

    public void testGetStories2() throws SQLException {
        addStory();
        List<String> tags = new ArrayList<>();
        tags.add("Technology");

        List<Integer> storyIds = tagsDAO.findStoryIdsByMultipleTags(tags);
        assertEquals(2, storyIds.size());
        assertEquals((Integer)2, storyIds.get(0));
        assertEquals((Integer)3, storyIds.get(1));
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }


    // --------- helpers -----------------

    private void addStory() throws SQLException {
        StoryDAO storyDAO = new StoryDAO();
        storyDAO.createStory("news","abc",1);
        List<String> tags = new ArrayList<>();
        tags.add("Technology");
        storyDAO.linkTagsToStory(3,tags);
    }

}
