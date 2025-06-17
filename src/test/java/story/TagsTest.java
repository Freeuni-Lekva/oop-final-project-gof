package story;

import data.MySqlConnector;
import data.story.TagsDAO;
import junit.framework.TestCase;
import model.story.Story;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagsTest extends TestCase {
    private Connection conn;
    private TagsDAO tagsDAO;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

       tagsDAO = new TagsDAO();
    }

    public void testTags() throws SQLException {
        List<String> tags = tagsDAO.getStoryTags(1);
        assertEquals(2, tags.size());
        assertEquals("Adventure",tags.get(0));
        assertEquals("Mystery",tags.get(1));

        List<String> tags2 = tagsDAO.getStoryTags(2);
        assertEquals(2, tags2.size());
        assertEquals("Technology",tags2.get(1));
    }

    public void testgetStories1() throws SQLException {
        List<String> tags = new ArrayList<>();
        tags.add("Adventure");

        List<Story> stories = tagsDAO.getStories(tags);
        assertEquals(2, stories.size());

        assertEquals(1,stories.get(0).getStoryId());
        assertEquals(2,stories.get(1).getStoryId());
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

}
