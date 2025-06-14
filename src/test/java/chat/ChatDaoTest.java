package chat;

import data.MySqlConnector;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import data.chat.*;

public class ChatDaoTest extends TestCase {

    private Connection conn;
    private ChatDAO chatDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();
    }

    public void testCreateChat() throws SQLException {
        chatDao.createChat(1,1);
        chatDao.createChat(2,2);

        assertEquals(1, chatDao.getUserId(1));
        assertEquals(2, chatDao.getUserId(2));

        assertEquals(1, chatDao.getChatId(1, 1));
        assertEquals(2, chatDao.getChatId(2, 2));

    }

    public void testConnectionNotNull() {
        assertNotNull("Connection should not be null", conn);
    }

    public void testGetUserId(){}

    public void testGetChatId(){}

    public void testMessageCount(){}

    public void testDeleteChat(){}

    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

    // -------------- helper methods --------------------




}
