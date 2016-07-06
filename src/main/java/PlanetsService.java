import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Taylor on 5/30/16.
 */
public class PlanetsService {

    private final Connection connection;

    public PlanetsService(Connection connection) {
        this.connection = connection;
    }

    public void initDatabase() throws SQLException {
        Statement stat = connection.createStatement();

        stat.execute("CREATE TABLE IF NOT EXISTS planets (id IDENTITY, name VARCHAR, radius INT, supports_life BOOLEAN, distance_from_sun DOUBLE)");
        stat.execute("CREATE TABLE IF NOT EXISTS moons (id IDENTITY, name VARCHAR, color VARCHAR, planet_id INT)");
    }

    public void insertPlanet(Planet planet) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO planets VALUES (NULL, ?, ?, ?, ?)");
        prepStat.setString(1, planet.getName());
        prepStat.setInt(2, planet.getRadius());
        prepStat.setBoolean(3, planet.isSupportsLife());
        prepStat.setDouble(4, planet.getDistanceFromSun());


        prepStat.execute();

        ResultSet results = prepStat.getGeneratedKeys();
        results.next();
        planet.setId(results.getInt(1));
        System.out.println(planet.getId());

        for(Moon moon : planet.getMoons()){
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO moons VALUES (NULL, ?, ?, ?)");
            ps2.setString(1, moon.getName());
            ps2.setString(2, moon.getColor());
            ps2.setInt(3, planet.getId());
            ps2.execute();

            ResultSet rs = ps2.getGeneratedKeys();
            rs.next();
            moon.setId(rs.getInt(1));
        }
    }

    public Planet selectPlanet(int planetId) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("SELECT * FROM planets LEFT JOIN moons ON planets.id = moons.planet_id WHERE planets.id = ?");
        prepStat.setInt(1, planetId);
        Planet planet = new Planet();

        ResultSet resultSet = prepStat.executeQuery();
        while (resultSet.next()){
            planet.setId(resultSet.getInt("id"));
            planet.setName(resultSet.getString("name"));
            planet.setRadius(resultSet.getInt("radius"));
            planet.setSupportsLife(resultSet.getBoolean("supports_life"));
            planet.setDistanceFromSun(resultSet.getDouble("distance_from_sun"));

            Moon moon = new Moon();
            moon.setId(resultSet.getInt("moons.id"));
            moon.setName(resultSet.getString("moons.name"));
            moon.setColor(resultSet.getString("moons.color"));

            planet.getMoons().add(moon);
        }

        return planet;
    }
    public ArrayList<Planet> selectAllPlanets() throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("SELECT * FROM planets LEFT OUTER JOIN moons ON planets.id = moons.planet_id");

        ResultSet resultSet = prepStat.executeQuery();
        ArrayList<Planet> allPlanets = new ArrayList<>();



        Planet endPlanet = new Planet();

        while(resultSet.next()){
            Planet planet = new Planet();
            Moon moon = new Moon();

            planet.setId(resultSet.getInt("id"));
            planet.setName(resultSet.getString("name"));
            planet.setRadius(resultSet.getInt("radius"));
            planet.setSupportsLife(resultSet.getBoolean("supports_life"));
            planet.setDistanceFromSun(resultSet.getDouble("distance_from_sun"));

            moon.setId(resultSet.getInt("moons.id"));
            moon.setName(resultSet.getString("moons.name"));
            moon.setColor(resultSet.getString("moons.color"));

            planet.getMoons().add(moon);

            allPlanets.add(planet);
        }
        return allPlanets;
    }
/*
    public void initDatabase(Connection connection) throws SQLException {

        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS planets (id IDENTITY, name VARCHAR, radius INT, supportsLife BOOLEAN, distanceFromSun DOUBLE)");

        //statement.execute("CREATE TABLE IF NOT EXISTS moons (moon_id IDENTITY, name VARCHAR, color VARCHAR)");
        statement.execute("CREATE TABLE IF NOT EXISTS moons (id IDENTITY, name VARCHAR, color VARCHAR, planetId INT)");

        //statement.execute("CREATE TABLE IF NOT EXISTS planet_moon (planet_id INT, moon_id INT)");
    }

    public void insertPlanetsAndMoons(Planet planet) throws SQLException {

        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO planets VALUES (NULL, ?, ?, ?, ?)");

        prepStat.setString(1, planet.name);
        prepStat.setInt(2, planet.radius);
        prepStat.setBoolean(3, planet.supportsLife);
        prepStat.setDouble(4, planet.distanceFromSun);

        //getGeneratedKeys() will return the generated id
        ResultSet resultSet = prepStat.getGeneratedKeys();
        resultSet.next();
        //read the first line of results
        //set the generated id into planet
        planet.setPlanetId(resultSet.getInt(1));

        ArrayList<Moon> moonArrayList = planet.getMoons();

        for (Moon moon : planet.moons){
            moon.setPlanetId(planet.planetId);
            insertMoon(moon);
        }
    }

    public void insertMoon(Moon moon) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO moons VALUES (NULL, ?, ?, ?)");

        prepStat.setString(1, moon.name);
        prepStat.setString(2, moon.color);
        prepStat.setInt(3, moon.planetId);
        prepStat.execute();

        ResultSet resultSet = prepStat.getGeneratedKeys();
        resultSet.next();
        moon.setId(resultSet.getInt(1));
    }

    public Planet selectPlanet(int planetId) throws SQLException{
        PreparedStatement prepStat = connection.prepareStatement("SELECT * FROM planets LEFT JOIN moons ON planets.id = moons.planetId WHERE planets.id = ?");

        prepStat.setInt(1, planetId);
        ResultSet resultsPlanets = prepStat.executeQuery();

        Planet returnedPlanet = new Planet();
        while(resultsPlanets.next()){
            returnedPlanet.setPlanetId(resultsPlanets.getInt("id"));
            returnedPlanet.setName(resultsPlanets.getString("name"));
            returnedPlanet.setRadius(resultsPlanets.getInt("radius"));
            returnedPlanet.setSupportsLife(resultsPlanets.getBoolean("supportsLife"));
            returnedPlanet.setDistanceFromSun(resultsPlanets.getDouble("distanceFromSun"));

            Moon returnedMoon = new Moon();
            returnedMoon.setId(resultsPlanets.getInt("moons.id"));
            returnedMoon.setName(resultsPlanets.getString("moons.name"));
            returnedMoon.setColor(resultsPlanets.getString("moons.color"));

            returnedPlanet.moons.add(returnedMoon);
        }

        return returnedPlanet;
    }
*/



/*
    public void linkPlanetAndMoons(Connection connection, Planet planet, Moon moon) throws SQLException{
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO planet_moon VALUES (?, ?)");

        prepStat.setInt(1, planet.getPlanetId());
        prepStat.setInt(2, moon.getMoonId());
        prepStat.execute();


    }*/
    /*
    public Planet createPlanetsAndMoons(Connection connection) throws SQLException {


        String name;
    int radius;
    boolean supportsLife;
    double distanceFromSun;

    ArrayList<Moon> moons = new ArrayList<>();

        ArrayList<Moon> mercuryMoons = new ArrayList<>();
        Planet mercury = new Planet("Mercury", 2440, false, 0.47, mercuryMoons);
        //insertPlanets(connection, mercury);

        ArrayList<Moon> venusMoons = new ArrayList<>();
        Planet venus = new Planet("Venus", 6052, false, 0.73, venusMoons);
        //insertPlanets(connection, venus);

        ArrayList<Moon> earthMoons = new ArrayList<>();
        Moon luna = new Moon ("Luna", "white");
        earthMoons.add(luna);
        Planet earth = new Planet("Earth", 6371, true, 1.00, earthMoons);
        //insertPlanets(connection, earth);

        ArrayList<Moon> marsMoons = new ArrayList<>();
        Moon phobos = new Moon ("Phobos", "gray");
        Moon deimos = new Moon ("Deimos", "gray");
        marsMoons.add(phobos);
        marsMoons.add(deimos);
        Planet mars = new Planet("Mars", 3390, false, 1.67, marsMoons);
       // insertPlanets(connection, mars);

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
       // insertPlanets(connection, jupiter);

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
        //insertPlanets(connection, saturn);


        ArrayList<Moon> uranusMoons = new ArrayList<>();
        Moon oberon = new Moon("Oberon", "dark gray");
        Moon titania = new Moon("Titania", "gray");
        Moon umbriel = new Moon("Umbriel", "dark gray/blue");
        uranusMoons.add(oberon);
        uranusMoons.add(titania);
        uranusMoons.add(umbriel);
        Planet uranus = new Planet("Uranus", 25362, false, 20.11, uranusMoons);
        //insertPlanets(connection, uranus);

        ArrayList<Moon> neptuneMoons = new ArrayList<>();
        Moon triton = new Moon("Triton", "brown/blue");
        Moon proteus = new Moon("Proteus", "gray");
        Moon nereid = new Moon("Nereid", "white");
        neptuneMoons.add(triton);
        neptuneMoons.add(proteus);
        neptuneMoons.add(nereid);
        Planet neptune = new Planet("Neptune", 24622, false, 30.3, neptuneMoons);
        //insertPlanets(connection, neptune);

        return null;
    }
*/
}

