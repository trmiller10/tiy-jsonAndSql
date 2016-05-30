import org.h2.tools.Server;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Taylor on 5/30/16.
 */
public class PlanetsServiceTests {


    Connection connection;

    PlanetsService testService;

    @Before
    public void before() throws SQLException {
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/test", "", null);
        testService = new PlanetsService(connection);
    }

    /**
     * Given: an H2 database and Planets and Moons classes
     * When: test Moons objects inserted into Planets table with SQL OTHER command
     * Then: test Moons objects returned
     */
    @Test
    public void testMoonObjectRetrieval() throws SQLException {

        //Arrange

        //Act
        //testService.initDatabase();


    }
}
