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



    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

}
