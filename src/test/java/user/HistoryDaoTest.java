package user;

import data.chat.ChatDAO;
import junit.framework.TestCase;
import model.story.Story;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import data.user.HistoryDAO;
import data.MySqlConnector;

public class HistoryDaoTest extends TestCase {

    private Connection conn;
    private HistoryDAO historyDao;
    private ChatDAO chatDAO;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();
        historyDao = new HistoryDAO();
        chatDAO = new ChatDAO();
    }

    public void testAddAndGetReadHistory() throws SQLException {
        assertTrue(historyDao.getReadHistoryForUser(1).isEmpty());

        historyDao.addReadHistory(1, 2);

        List<Story> history = historyDao.getReadHistoryForUser(1);
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getStoryId());
    }

    public void testReadHistoryOrder() throws InterruptedException, SQLException {
        historyDao.addReadHistory(1, 1);
        Thread.sleep(1500);
        historyDao.addReadHistory(1, 2);

        List<Story> history = historyDao.getReadHistoryForUser(1);

        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getStoryId());
        assertEquals(1, history.get(1).getStoryId());
    }

    /**
     * Tests that re-reading story updates its timestamp
     * and moves it to the top of the history list.
     */
    public void testReadHistoryUpdateTimestamp() throws InterruptedException, SQLException {
        historyDao.addReadHistory(1, 1);
        Thread.sleep(20);
        historyDao.addReadHistory(1, 2);

        Thread.sleep(20);
        historyDao.addReadHistory(1, 1);

        List<Story> history = historyDao.getReadHistoryForUser(1);
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getStoryId());
        assertEquals(2, history.get(1).getStoryId());
    }

    public void testDelete() throws SQLException {
        historyDao.addReadHistory(1, 1);
        chatDAO.createChat(1,1);

        historyDao.deleteReadHistory(1,1);
        assertTrue(historyDao.getReadHistoryForUser(1).isEmpty());
        assertEquals(-1,chatDAO.getChatId(1,1));
    }

    @Override
    public void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            MySqlConnector.close(conn);
        }
    }
}