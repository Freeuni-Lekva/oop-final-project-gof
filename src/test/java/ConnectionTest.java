import data.MySqlConnector;
import junit.framework.TestCase;

import java.sql.Connection;

public class ConnectionTest extends TestCase{
    private Connection conn;

    @Override
    public void setUp() {
        conn = MySqlConnector.getConnection();
    }


    public void testConnectionNotNull() {
        assertNotNull("Connection should not be null", conn);
    }

    @Override
    public void tearDown() {
        MySqlConnector.close(conn);
        assertTrue("Connection should be closed after test", true);
    }

}
