package chat;

import data.MySqlConnector;
import data.chat.SharedChatDAO;
import junit.framework.TestCase;
import model.chat.SharedChat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SharedChatDAOTest extends TestCase {

    private Connection conn;
    private SharedChatDAO sharedChatDAO;

    @Override
    public void setUp() throws Exception {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL(); // resets DB
        sharedChatDAO = new SharedChatDAO();

        createTestChat(1, 1, 1);
        createTestChat(2, 2, 2);
    }

    public void testShareChatAndIsChatShared() throws SQLException {
        assertFalse(sharedChatDAO.isChatShared(1));

        sharedChatDAO.shareChat(1, 1);

        assertTrue(sharedChatDAO.isChatShared(1));
    }

    public void testDeleteShareChat() throws SQLException {
        sharedChatDAO.shareChat(2, 2);
        assertTrue(sharedChatDAO.isChatShared(2));

        sharedChatDAO.unshareChat(2);
        assertFalse(sharedChatDAO.isChatShared(2));
    }

    public void testGetSharedChatsByUser() throws SQLException {
        sharedChatDAO.shareChat(1, 1);
        sharedChatDAO.shareChat(2, 2);

        createTestChat(30, 2, 1); // not shared

        List<SharedChat> chats = sharedChatDAO.getSharedChatsByUser(1);
        assertEquals(1, chats.size());

        for (SharedChat chat : chats) {
            assertEquals(1, chat.getUserId());
            assertNotNull(chat.getStoryTitle());
            assertTrue(chat.getChatId() == 1);
        }
    }

    public void testGetSharedChatsFeedForUser() throws SQLException {
        insertFollower(2, 1);
        insertFollower(2, 3);

        createTestChat(30, 2, 1); // not shared

        createTestChat(50, 1, 3);
        sharedChatDAO.shareChat(50, 3);

        sharedChatDAO.shareChat(1, 1);
        sharedChatDAO.shareChat(2, 2);

        List<SharedChat> feed = sharedChatDAO.getSharedChatsFeedForUser(2);

        assertEquals(2, feed.size());
        for (SharedChat chat : feed) {
            assertTrue(chat.getUserId() == 1 || chat.getUserId() == 3);
            assertTrue(chat.getChatId() == 1 || chat.getChatId() == 50);
            assertNotNull(chat.getUsername());
            assertNotNull(chat.getUserImage());
            assertNotNull(chat.getStoryTitle());
        }
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue(conn.isClosed());
    }

    // --- Helper Methods ---

    private void createTestChat(int chatId, int storyId, int userId) throws SQLException {
        String sql = "INSERT INTO chats (chat_id, story_id, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chatId);
            ps.setInt(2, storyId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private void insertFollower(int followerId, int followingId) throws SQLException {
        String sql = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ps.executeUpdate();
        }
    }
}
