import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.tools.internal.ws.processor.model.Model;
import spark.ModelAndView;
import spark.Spark;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.halt;

public class Main {

    public static void main(String[] args) throws SQLException{
        Connection connection;

        //Create service variable, instantiated before testing
        PlanetsService service;


        //create a TCP server
        //make sure H2 dependency is in pom.xml
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        //establish connection
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/main", "", null);
        //instantiate new service
        service = new PlanetsService(connection);

        //initialize database
        service.initDatabase(connection);


        Spark.get(
                "/",
                (request, response) -> {

                    service.initDatabase(connection);


                    Planet planet = new Planet();
                    planet.name = request.params(":name");
                    planet.distanceFromSun = 1;
                    planet.radius = 6387; // km
                    planet.supportsLife = true;

                    Moon moon = new Moon();
                    moon.name = "Luna";

                    planet.moons.add(moon);

//                    service.insertPlanets(connection, planet);

                    return null;
                }

        );


        /**
         * This endpoint must query from the database of planets and moons and
         * return an ArrayList of all planets in JSON format.
         */

        Spark.get(
                "/planets",
                (request, response) -> {

                    HashMap hashMapModel = new HashMap();
                    // returns an arraylist of all planets
                    //ArrayList<Planet> planetArrayList = service.retrievePlanets(connection);

                    //encode arraylist in JSON



                    return new ModelAndView(hashMapModel, "planets.mustache");
                }
        );

        Spark.get(
                "/planet",
                (request, response) -> {
                    // queryParam for id?
                    // or maybe a param like :name and request.params(":name") ?
/*
                    Planet planet = new Planet();
                    planet.name = request.params(":name");
                    planet.distanceFromSun = 1;
                    planet.radius = 6387; // km
                    planet.supportsLife = true;

                    Moon moon = new Moon();
                    moon.name = "Luna";

                    planet.moons.add(moon);*/

                    ArrayList<Moon> earthMoons = new ArrayList<>();
                    Moon luna = new Moon ("Luna", "white");
                    earthMoons.add(luna);
                    Planet planet = new Planet("Earth", 6371, true, 1.00, earthMoons);

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