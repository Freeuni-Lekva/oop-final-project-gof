package chat;

import data.MySqlConnector;
import data.chat.ChatDAO;
import data.chat.MessageDAO;
import junit.framework.TestCase;
import model.chat.ChatHistory;
import model.chat.Message;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatHistoryTest extends TestCase{

    private ChatHistory chatHistory;
    private ChatDAO chatDao;
    private MessageDAO messageDao;
    private Connection conn;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();

        chatDao = new ChatDAO(new MySqlConnector());
        messageDao = new MessageDAO(new MySqlConnector());
        chatHistory = new ChatHistory();
        initTables();
    }

    public void testGenerateChat() throws SQLException {
        ArrayList<Message> messages = messageDao.getMessages(1);

        assertEquals("A suspenseful journey to uncover hidden secrets.",messages.get(0).getMessage());

        String history = chatHistory.generateChat(messages);
        String real_history = "Information about the world : \n" +
                "A suspenseful journey to uncover hidden secrets.\n" +
                "User : aba\n" +
                "User : bca\n" +
                "Narrator : cca\n";

        assertEquals(history,real_history);
    }


    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

    // ------------- helper methods ---------------

    private void createChats() throws SQLException {
        chatDao.createChat(1,1);
    }

    private void createMessages() throws SQLException {
        messageDao.addMessage(1,"aba",true);
        messageDao.addMessage(1,"bca",true);
        messageDao.addMessage(1,"cca",false);
    }

    private void initTables() throws SQLException {
        createChats();
        createMessages();
    }

}
