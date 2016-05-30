import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException{
        Connection connection;

        //Create service variable, instantiated before testing
        PlanetsService planetService;


        //create a TCP server
        //make sure H2 dependency is in pom.xml
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        //establish connection
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/main", "", null);
        //instantiate new service
        planetService = new PlanetsService(connection);

        //initialize database
        planetService.initDatabase();
        Spark.get(
                "/planets",
                (request, response) -> {
                    // returns an arraylist of all planets
                    return null;
                }
        );

        Spark.get(
                "/planet",
                (request, response) -> {
                    // queryParam for id?
                    // or maybe a param like :name and request.params(":name") ?

                    Planet planet = new Planet();
                    planet.name = request.params(":name");
                    planet.distanceFromSun = 1;
                    planet.radius = 6387; // km
                    planet.supportsLife = true;

                    Moon moon = new Moon();
                    moon.name = "Luna";

                    planet.moons.add(moon);

                    Gson gson = new GsonBuilder().create();
                    return gson.toJson(planet);

                }
        );

        Spark.post(
                "/planet",
                (request, response) -> {
                    String planetJson = request.queryParams("planet");
                    Gson gson = new GsonBuilder().create();
                    Planet planet = gson.fromJson(planetJson, Planet.class);

                    return "";
                }

        );

    }

}