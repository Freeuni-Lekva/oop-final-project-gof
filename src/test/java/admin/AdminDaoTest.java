package admin;

import data.MySqlConnector;
import data.admin.AdminDAO;
import data.user.UserDAO;
import junit.framework.TestCase;
import model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AdminDaoTest extends TestCase {
    private Connection conn;
    private UserDAO userDao;
    private AdminDAO adminDao;

    @Override
    public void setUp() throws SQLException, IOException {
        conn = MySqlConnector.getConnection();
        MySqlConnector.setupSQL();
        userDao = new UserDAO();
        adminDao = new AdminDAO();
    }

    public void testAdmin() throws SQLException {
        User user = userDao.findUserById(1);
        assertFalse(user.isAdmin());

        adminDao.SetIsAdmin(1);

        User newUser = userDao.findUserById(1);
        assertTrue(newUser.isAdmin());
    }

    @Override
    public void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            MySqlConnector.close(conn);
        }
    }


}
