package chat;

import data.MySqlConnector;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import data.chat.*;
import model.chat.Message;

public class MessageDaoTest extends TestCase {

    private Connection conn;
    private ChatDAO chatDao;
    private MessageDAO messageDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

        chatDao = new ChatDAO(new MySqlConnector());
        messageDao = new MessageDAO(new MySqlConnector());
    }

    public void testAddMessage() throws SQLException {
        initTables();

        assertEquals("aba",messageDao.getMessage(1));
        assertEquals("bca",messageDao.getMessage(2));
        assertEquals("cca",messageDao.getMessage(3));

        assertEquals("qwe",messageDao.getMessage(4));
        assertEquals("wer",messageDao.getMessage(5));
    }

    public void testMessages() throws SQLException {
        initTables();

        ArrayList<Message> messages1 = messageDao.getMessages(1);
        // 4 because additional message as "prompt" is added to messages
        assertEquals(4,messages1.size());

        assertEquals("aba",messages1.get(1).getMessage());
        assertEquals("bca",messages1.get(2).getMessage());
        assertEquals("cca",messages1.get(3).getMessage());

        assertTrue(messages1.get(1).isUser());
        assertTrue(messages1.get(0).isPrompt());
        assertFalse(messages1.get(3).isUser());
    }

    public void testMessageDeletion() throws SQLException {
        initTables();

        chatDao.deleteChat(1);
        assertEquals("",messageDao.getMessage(1));
        assertEquals("",messageDao.getMessage(2));
    }

    public void testMessageUpdate() throws SQLException {
        initTables();

        messageDao.updateMessageContent(1,"lll");
        assertEquals("lll",messageDao.getMessage(1));

        messageDao.updateMessageContent(5,"ooo");
        assertEquals("ooo",messageDao.getMessage(5));
    }


    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

    // ------------- helper methods ---------------

    private void createChats() throws SQLException {
        chatDao.createChat(1,1);
        chatDao.createChat(2,2);
        chatDao.createChat(1,2);
        chatDao.createChat(2,1);
    }

    private void createMessages() throws SQLException {
        messageDao.addMessage(1,"aba",true);
        messageDao.addMessage(1,"bca",true);
        messageDao.addMessage(1,"cca",false);

        messageDao.addMessage(2,"qwe",true);
        messageDao.addMessage(2,"wer",false);

        messageDao.addMessage(3,"rty",true);
        messageDao.addMessage(3,"gva",false);

        messageDao.addMessage(4,"fa",false);
    }

    private void initTables() throws SQLException {
        createChats();
        createMessages();
    }
}