package chat;

import data.MySqlConnector;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import data.chat.*;

public class ChatDaoTest extends TestCase {

    private Connection conn;
    private ChatDAO chatDao;
    private MessageDAO messageDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

        chatDao = new ChatDAO();
        messageDao = new MessageDAO();
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

    public void testGetUserId() throws SQLException {
        insertNewChats();
        assertChatIds();

        assertEquals(1, chatDao.getUserId(1));
        assertEquals(2, chatDao.getUserId(2));
        assertEquals(1, chatDao.getUserId(3));
        assertEquals(2, chatDao.getUserId(4));
    }

    public void testMessageCount() throws SQLException {
        initTables();

        assertEquals(2,chatDao.messageCount(1));
        assertEquals(1,chatDao.messageCount(2));
    }

    public void testDeleteChat() throws SQLException {
        initTables();

        chatDao.deleteChat(1);
        assertEquals(-1,chatDao.getChatId(1, 1));

        chatDao.deleteChat(2);
        assertEquals(-1,chatDao.getChatId(2, 2));
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

    // -------------- helper methods --------------------

    private void insertNewChats() throws SQLException {
        chatDao.createChat(1,1);
        chatDao.createChat(2,2);
        chatDao.createChat(1,2);
        chatDao.createChat(2,1);
    }

    private void insertMessages() throws SQLException {
        messageDao.addMessage(1,"abc",true);
        messageDao.addMessage(1,"def",false);

        messageDao.addMessage(2,"ghi",false);
        messageDao.addMessage(3,"jkl",true);
    }

    private void assertChatIds() throws SQLException {
        assertEquals(1, chatDao.getChatId(1, 1));
        assertEquals(2, chatDao.getChatId(2, 2));
        assertEquals(3, chatDao.getChatId(1, 2));
        assertEquals(4, chatDao.getChatId(2, 1));
    }

    private void initTables() throws SQLException {
        insertNewChats();
        insertMessages();
        assertChatIds();
    }

}
