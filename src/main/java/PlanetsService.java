import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Taylor on 5/30/16.
 */
public class PlanetsService {

    private final Connection connection;

    public PlanetsService(Connection connection) {
        this.connection = connection;
    }

    public void initDatabase(Connection connection) throws SQLException {
        Statement stat = connection.createStatement();

        stat.execute("DROP TABLE IF EXISTS planets");
        stat.execute("DROP TABLE IF EXISTS moons");
        stat.execute("DROP TABLE IF EXISTS planet_moons");

        stat.execute("DROP TABLE IF EXISTS planet");
        stat.execute("DROP TABLE IF EXISTS moon");
        stat.execute("DROP TABLE IF EXISTS planet_moon");

        stat.execute("CREATE TABLE IF NOT EXISTS planet (" +
                "id IDENTITY, " +
                "name VARCHAR, " +
                "radius INT, " +
                "supports_life BOOLEAN, " +
                "distance_from_sun DOUBLE," +
                "PRIMARY KEY (id))");

        stat.execute("CREATE TABLE IF NOT EXISTS moon (" +
                "id IDENTITY, " +
                "name VARCHAR, " +
                "color VARCHAR," +
                "PRIMARY KEY (id))");

        stat.execute("CREATE TABLE IF NOT EXISTS planet_moon(" +
                "planet_id INT, " +
                "moon_id INT, " +
                "CONSTRAINT planet_id_fk FOREIGN KEY (planet_id) REFERENCES planet (id), " +
                "CONSTRAINT moon_id_fk FOREIGN KEY (moon_id) REFERENCES moon (id))");
        //PRIMARY KEY (id),
        //CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES users (id)

    }

    public void insertPlanet(Connection connection, Planet planet) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO planet VALUES (NULL, ?, ?, ?, ?)");
        prepStat.setString(1, planet.getName());
        prepStat.setInt(2, planet.getRadius());
        prepStat.setBoolean(3, planet.isSupportsLife());
        prepStat.setDouble(4, planet.getDistanceFromSun());
        prepStat.execute();

        ResultSet resultSet = prepStat.getGeneratedKeys();
        resultSet.next();
        planet.setId(resultSet.getInt(1));
        
        for(Moon moon : planet.getMoons()){
            PreparedStatement moonPrepStat = connection.prepareStatement("INSERT INTO moon VALUES (NULL, ?, ?)");
            moonPrepStat.setString(1, moon.getName());
            moonPrepStat.setString(2, moon.getColor());
            moonPrepStat.execute();

            ResultSet moonResultSet = moonPrepStat.getGeneratedKeys();
            moonResultSet.next();
            moon.setId(moonResultSet.getInt(1));

            linkPlanetAndMoons(connection, planet, moon);
        }
    }

    public void linkPlanetAndMoons(Connection connection, Planet planet, Moon moon) throws SQLException{
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO planet_moon VALUES (?, ?)");

        prepStat.setInt(1, planet.getId());
        prepStat.setInt(2, moon.getId());
        prepStat.execute();
    }


    public Planet returnPlanet(Connection connection, int planetId) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("SELECT * FROM planet WHERE id = ?");
        prepStat.setInt(1, planetId);
        Planet planet = new Planet();

        ResultSet resultSet = prepStat.executeQuery();

        while (resultSet.next()){
            planet.setId(resultSet.getInt("id"));
            planet.setName(resultSet.getString("name"));
            planet.setRadius(resultSet.getInt("radius"));
            planet.setSupportsLife(resultSet.getBoolean("supports_life"));
            planet.setDistanceFromSun(resultSet.getDouble("distance_from_sun"));

            PreparedStatement planetMoonStat = connection.prepareStatement("SELECT * FROM planet_moon WHERE planet_id = ?");
            planetMoonStat.setInt(1, planetId);

            ResultSet resultIds = planetMoonStat.executeQuery();
            int moonId;
            ArrayList<Moon> returnedMoonList = new ArrayList<>();

            while (resultIds.next()){
                moonId = resultIds.getInt("planet_moon.moon_id");

                Moon moon = returnMoon(connection, moonId);

                returnedMoonList.add(moon);
            }
            planet.setMoons(returnedMoonList);
        }

        return planet;
    }

    public Moon returnMoon(Connection connection, int moonId) throws SQLException{
        PreparedStatement moonPrepStat = connection.prepareStatement("SELECT * FROM moon WHERE id = ?");
        moonPrepStat.setInt(1, moonId);
        //maybe there is a conflict with previously-named moonResultSet?
        //RESOLVED: no, forgot if(moonResult.next())
        ResultSet moonResult = moonPrepStat.executeQuery();
        Moon moon = new Moon();

        if (moonResult.next()) {
            moon.setId(moonId);
            moon.setName(moonResult.getString("name"));
            moon.setColor(moonResult.getString("color"));
        }
        return moon;
    }


    public ArrayList<Planet> returnAllPlanets(Connection connection) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("SELECT * FROM planet");

        ResultSet resultSet = prepStat.executeQuery();

        ArrayList<Planet> allPlanets = new ArrayList<>();

        while(resultSet.next()){
            Planet planet = returnPlanet(connection, resultSet.getInt("id"));

            allPlanets.add(planet);
        }
        return allPlanets;
    }

    public void createPlanetsAndMoons(Connection connection) throws SQLException {

        ArrayList<Moon> mercuryMoons = new ArrayList<>();
        Planet mercury = new Planet("Mercury", 2440, false, 0.47, mercuryMoons);
        insertPlanet(connection, mercury);

        ArrayList<Moon> venusMoons = new ArrayList<>();
        Planet venus = new Planet("Venus", 6052, false, 0.73, venusMoons);
        insertPlanet(connection, venus);

        ArrayList<Moon> earthMoons = new ArrayList<>();
        Moon luna = new Moon ("Luna", "white");
        earthMoons.add(luna);
        Planet earth = new Planet("Earth", 6371, true, 1.00, earthMoons);
        insertPlanet(connection, earth);

        ArrayList<Moon> marsMoons = new ArrayList<>();
        Moon phobos = new Moon ("Phobos", "gray");
        Moon deimos = new Moon ("Deimos", "gray");
        marsMoons.add(phobos);
        marsMoons.add(deimos);
        Planet mars = new Planet("Mars", 3390, false, 1.67, marsMoons);
        insertPlanet(connection, mars);

        ArrayList<Moon> jupiterMoons = new ArrayList<>();
        Moon io = new Moon("Io", "orange");
        Moon europa = new Moon("Europa", "white/red");
        Moon ganymede = new Moon("Ganymede", "dark gray");
        Moon callisto = new Moon("Callisto", "gray/blue");
        jupiterMoons.add(io);
        jupiterMoons.add(europa);
        jupiterMoons.add(ganymede);
        jupiterMoons.add(callisto);
        Planet jupiter = new Planet("Jupiter", 69911, false, 5.45, jupiterMoons);
        insertPlanet(connection, jupiter);

        ArrayList<Moon> saturnMoons = new ArrayList<>();
        Moon titan = new Moon("Titan", "yellow");
        Moon rhea = new Moon("Rhea", "white");
        Moon iapetus = new Moon("Iapetus", "white/black");
        Moon dione = new Moon("Dione", "white");
        Moon tethys = new Moon("Tethys", "white/yellow");
        Moon enceladus = new Moon("Enceladus", "white/tan/blue");
        Moon mimas = new Moon("Mimas", "white/yellow");
        saturnMoons.add(titan);
        saturnMoons.add(rhea);
        saturnMoons.add(iapetus);
        saturnMoons.add(dione);
        saturnMoons.add(tethys);
        saturnMoons.add(enceladus);
        saturnMoons.add(mimas);
        Planet saturn = new Planet("Saturn", 58232, false, 10.1, saturnMoons);
        insertPlanet(connection, saturn);


        ArrayList<Moon> uranusMoons = new ArrayList<>();
        Moon oberon = new Moon("Oberon", "dark gray");
        Moon titania = new Moon("Titania", "gray");
        Moon umbriel = new Moon("Umbriel", "dark gray/blue");
        uranusMoons.add(oberon);
        uranusMoons.add(titania);
        uranusMoons.add(umbriel);
        Planet uranus = new Planet("Uranus", 25362, false, 20.11, uranusMoons);
        insertPlanet(connection, uranus);

        ArrayList<Moon> neptuneMoons = new ArrayList<>();
        Moon triton = new Moon("Triton", "brown/blue");
        Moon proteus = new Moon("Proteus", "gray");
        Moon nereid = new Moon("Nereid", "white");
        neptuneMoons.add(triton);
        neptuneMoons.add(proteus);
        neptuneMoons.add(nereid);
        Planet neptune = new Planet("Neptune", 24622, false, 30.3, neptuneMoons);
        insertPlanet(connection, neptune);
    }
}

