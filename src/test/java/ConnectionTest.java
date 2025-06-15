import data.MySqlConnector;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTest extends TestCase{
    private Connection conn;

    @Override
    public void setUp() throws SQLException{
        conn = MySqlConnector.getConnection();
    }

    public void testConnectionNotNull() {
        assertNotNull("Connection should not be null", conn);
    }

    @Override
    public void tearDown() throws SQLException {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", conn.isClosed());
    }

}
