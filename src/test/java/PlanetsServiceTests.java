import org.h2.tools.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Stream;

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

    /**
     * This test checks to make sure all the tables are created properly.
     * @throws SQLException
     */

    @Test
    public void testInit() throws SQLException{

        testService.initDatabase(connection);

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

        assertThat(tableNames, hasItems("PLANET", "MOON", "PLANET_MOON"));
    }

    /**
     * Given: an H2 database and Planets and Moons classes
     * When: Planet and Moon inserted into database and then queried by planet id
     * Then: Planet and Moon returned
     */

    @Test
    public void testObjectTables() throws SQLException {

        testService.initDatabase(connection);

        Moon luna = new Moon("Luna", "white");

        ArrayList<Moon> earthMoons = new ArrayList<>();
        earthMoons.add(luna);

        Planet earth = new Planet("Earth", 6371, true, 1.00, earthMoons);

        testService.insertPlanet(connection, earth);

        Planet testPlanet = testService.returnPlanet(connection, 1);

        ArrayList<Moon> returnedMoons = testPlanet.getMoons();


        assertThat(earth.getId(), is(testPlanet.getId()));
        assertThat(earthMoons.size(), is(returnedMoons.size()));
    }

    /**
     * Given: H2 database with multiple planets, with multiple moons
     * When: database queried with planet id
     * Then: planets with moons returned
     *
     * @throws SQLException
     */
    @Test
    public void testMultiplePlanetsAndMoons() throws SQLException {
        testService.initDatabase(connection);
        Moon testOne = new Moon("One", "red");
        Moon testTwo = new Moon("Two", "blue");
        Moon testThree = new Moon("Three", "white");
        Moon testFour = new Moon("Four", "yellow");

        ArrayList<Moon> testMoons = new ArrayList<>();
        testMoons.add(testOne);
        testMoons.add(testTwo);
        ArrayList<Moon> exMoons = new ArrayList<>();
        exMoons.add(testThree);
        exMoons.add(testFour);

        Planet planetTest = new Planet("Test", 1234, true, 1.00, testMoons);
        Planet planetEx = new Planet("X", 5678, false, 2.00, exMoons);

        testService.insertPlanet(connection, planetTest);
        testService.insertPlanet(connection, planetEx);

        Planet planetOne = testService.returnPlanet(connection, 1);
        Planet planetTwo = testService.returnPlanet(connection, 2);

        assertThat(planetOne.getId(), is(planetTest.getId()));
        assertThat(planetTwo.getRadius(), is(planetEx.getRadius()));
        assertThat(planetOne.getMoons().get(1).getName(), is(testMoons.get(1).getName()));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testThree.getName())));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testFour.getName())));
        assertFalse(planetOne.getMoons().stream().anyMatch(o -> o.getName().equals(testThree.getName())));
        assertFalse(planetTwo.getMoons().stream().anyMatch(o -> o.getName().equals(testOne.getName())));
        assertFalse(planetTwo.getMoons().stream().anyMatch(o -> o.getName().equals(testTwo.getName())));
    }

    /**
     * Given: H2 database with multiple planets, with multiple moons
     * When: H2 queried for all planets, with all moons
     * Then: all planets and moons returned, with no duplicates
     * @throws SQLException
     */

    @Test
    public void testReturnAllPlanets() throws SQLException{
        testService.initDatabase(connection);
        Moon testOne = new Moon("One", "red");
        Moon testTwo = new Moon("Two", "blue");
        Moon testThree = new Moon("Three", "white");
        Moon testFour = new Moon("Four", "yellow");

        ArrayList<Moon> testMoons = new ArrayList<>();
        testMoons.add(testOne);
        testMoons.add(testTwo);
        ArrayList<Moon> exMoons = new ArrayList<>();
        exMoons.add(testThree);
        exMoons.add(testFour);

        Planet planetTest = new Planet(1, "Test", 1234, true, 1.00, testMoons);
        Planet planetEx = new Planet(2, "X", 5678, false, 2.00, exMoons);

        testService.insertPlanet(connection, planetTest);
        testService.insertPlanet(connection, planetEx);

        ArrayList<Planet> controlPlanetList = new ArrayList<>();
        controlPlanetList.add(planetTest);
        controlPlanetList.add(planetEx);

        ArrayList<Planet> allPlanetsList = testService.returnAllPlanets(connection);

        assertThat(controlPlanetList.size(), is(allPlanetsList.size()));
    }

    /**
     * Given: PlanetsService, with method createPlanetsAndMoons() that generates multiple planets, with multiple moons
     * When: createPlanetsAndMoons() is run
     * Then: All generated objects are printed out
     * @throws SQLException
     */

    @Test
    public void testCreatePlanetsAndMoons() throws SQLException {
        testService.initDatabase(connection);
        testService.createPlanetsAndMoons(connection);

        ArrayList<Planet> solarSystem = testService.returnAllPlanets(connection);

        for (Planet planet : solarSystem) {

            System.out.println(planet.getName() + "," + planet.getDistanceFromSun()  + "," + planet.getRadius()  + "," + planet.isSupportsLife() + ",");

            ArrayList<Moon> allMoons = planet.getMoons();

            for (Moon moon : allMoons) {

                System.out.println(moon.getName() + "," + moon.getColor());
            }
        }
    }


    @After
    public void after() throws SQLException{
        Statement cleanStat = connection.createStatement();

        cleanStat.execute("DROP TABLE IF EXISTS planets");
        cleanStat.execute("DROP TABLE IF EXISTS moons");
        cleanStat.execute("DROP TABLE IF EXISTS moon");
        cleanStat.execute("DROP TABLE IF EXISTS planet");
        cleanStat.execute("DROP TABLE IF EXISTS planet_moon");
        cleanStat.execute("DROP TABLE IF EXISTS planet_moons");
    }
}
