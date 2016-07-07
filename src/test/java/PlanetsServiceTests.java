import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testInit() throws SQLException{

        testService.initDatabase();

        //assert
        ResultSet tables = connection.createStatement().executeQuery(
                "SELECT * \n" +
                        "FROM INFORMATION_SCHEMA.TABLES \n" +
                        "WHERE TABLE_SCHEMA = 'PUBLIC'");

        ArrayList<String> tableNames = new ArrayList<>();

        while (tables.next()) {
            //get TABLE_NAME strings and add them to tableNames array list
            tableNames.add(tables.getString("TABLE_NAME"));
        }

        assertThat(tableNames, hasItems("PLANETS", "MOONS"));
    }

    /**
     * Given: an H2 database and Planets and Moons classes
     * When: test Moons objects inserted into Planets table with SQL OTHER command
     * Then: test Moons objects returned
     */
    @Test
    public void testPlanetDatabase() throws SQLException {

        testService.initDatabase();

        Moon luna = new Moon(1, "Luna", "white");

        ArrayList<Moon> earthMoons = new ArrayList<>();
        earthMoons.add(luna);
        Planet earth = new Planet("Earth", 6371, true, 1.00, earthMoons);

        testService.insertPlanet(earth);

        ResultSet resultsPlanets = connection.createStatement().executeQuery(
                "SELECT * FROM PLANETS");
        ResultSet resultsMoon = connection.createStatement().executeQuery(
                "SELECT * FROM MOONS");

        ArrayList<Moon> returnedMoons = new ArrayList<>();
        if(resultsMoon.next()){
            Moon returnedMoon = new Moon();
            returnedMoon.setName(resultsMoon.getString("name"));
            returnedMoon.setColor(resultsMoon.getString("color"));
            returnedMoon.setplanet_id(resultsMoon.getInt("id"));

            returnedMoons.add(returnedMoon);
        }
        Planet returnedPlanet = new Planet();
        if(resultsPlanets.next()){
            returnedPlanet.setId(resultsPlanets.getInt("id"));
            returnedPlanet.setName(resultsPlanets.getString("name"));
            returnedPlanet.setRadius(resultsPlanets.getInt("radius"));
            returnedPlanet.setSupportsLife(resultsPlanets.getBoolean("supports_life"));
            returnedPlanet.setDistanceFromSun(resultsPlanets.getDouble("distance_from_sun"));
            returnedPlanet.setMoons(returnedMoons);
        }

        assertThat(earth.getId(), is(returnedPlanet.getId()));
    }

    @Test
    public void testInsertSelect() throws SQLException {
        testService.initDatabase();

        Moon luna = new Moon(1, "Luna", "white", 1);

        ArrayList<Moon> earthMoons = new ArrayList<>();
        earthMoons.add(luna);
        Planet earth = new Planet("Earth", 6371, true, 1.00, earthMoons);

        testService.insertPlanet(earth);

        Planet returnPlanet = testService.selectPlanet(1);

        assertThat(returnPlanet.getRadius(), is(earth.getRadius()));
    }

    @Test
    public void testMultiplePlanetsAndMoons() throws SQLException {
        testService.initDatabase();
        Moon testOne = new Moon(1, "One", "red", 1);
        Moon testTwo = new Moon(2, "Two", "blue", 1);
        Moon testThree = new Moon(3, "Three", "white", 2);
        Moon testFour = new Moon(4, "Four", "yellow", 2);

        ArrayList<Moon> testMoons = new ArrayList<>();
        testMoons.add(testOne);
        testMoons.add(testTwo);
        ArrayList<Moon> exMoons = new ArrayList<>();
        exMoons.add(testThree);
        exMoons.add(testFour);

        Planet planetTest = new Planet(1, "Test", 1234, true, 1.00, testMoons);
        Planet planetEx = new Planet(2, "X", 5678, false, 2.00, exMoons);

        testService.insertPlanet(planetTest);
        testService.insertPlanet(planetEx);

        Planet planetOne = testService.selectPlanet(1);
        Planet planetTwo = testService.selectPlanet(2);

        assertThat(planetOne.getId(), is(planetTest.getId()));
        assertThat(planetTwo.getRadius(), is(planetEx.getRadius()));
        assertThat(planetOne.getMoons().get(1).getName(), is(testMoons.get(1).getName()));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testThree.getName())));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testFour.getName())));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testThree.getName())));
        assertFalse(planetTwo.getMoons().stream().anyMatch(o -> o.getName().equals(testOne.getName())));
        assertFalse(planetTwo.getMoons().stream().anyMatch(o -> o.getName().equals(testTwo.getName())));
    }

    @Test
    public void testSelectAllPlanets() throws SQLException{
        testService.initDatabase();
        Moon testOne = new Moon(1, "One", "red", 1);
        Moon testTwo = new Moon(2, "Two", "blue", 1);
        Moon testThree = new Moon(3, "Three", "white", 2);
        Moon testFour = new Moon(4, "Four", "yellow", 2);

        ArrayList<Moon> testMoons = new ArrayList<>();
        testMoons.add(testOne);
        testMoons.add(testTwo);
        ArrayList<Moon> exMoons = new ArrayList<>();
        exMoons.add(testThree);
        exMoons.add(testFour);

        Planet planetTest = new Planet(1, "Test", 1234, true, 1.00, testMoons);
        Planet planetEx = new Planet(2, "X", 5678, false, 2.00, exMoons);

        testService.insertPlanet(planetTest);
        testService.insertPlanet(planetEx);

        ArrayList<Planet> controlPlanetList = new ArrayList<>();
        controlPlanetList.add(planetTest);
        controlPlanetList.add(planetEx);

        ArrayList<Planet> allPlanetsList = testService.selectAllPlanets();

        assertTrue(allPlanetsList.equals(controlPlanetList));
    }


    @After
    public void after() throws SQLException{
        Statement cleanStat = connection.createStatement();

        cleanStat.execute("DROP TABLE IF EXISTS planets");
        cleanStat.execute("DROP TABLE IF EXISTS moons");
        cleanStat.execute("DROP TABLE IF EXISTS moon");
        cleanStat.execute("DROP TABLE IF EXISTS planet");
    }
}
