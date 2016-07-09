import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.javac.comp.Resolve;
import spark.ModelAndView;
import spark.Spark;
import org.h2.tools.Server;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.before;
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

        service.createPlanetsAndMoons(connection);

        /**
         * This endpoint must query from the database of planets and moons and
         * return an ArrayList of all planets in JSON format.
         */

        Spark.get(
                "/planets",
                (request, response) -> {
                    // returns an arraylist of all planets
                    ArrayList<Planet> planetArrayList = service.returnAllPlanets(connection);

                    //encode arraylist in JSON
                    Gson gson = new GsonBuilder().create();
                    return gson.toJson(planetArrayList);
                }
        );


        Spark.get(
                "/",
                (request, response) -> {
                    HashMap model = new HashMap();
                    response.redirect("/planets");
                    return null;
                }

        );




        Spark.get(
                "/planet/:id",
                (request, response) -> {
                    // queryParam for id?
                    // or maybe a param like :name and request.params(":name") ?

                    Planet planet = service.returnPlanet(connection, Integer.valueOf(request.params("id")));
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

                    service.insertPlanet(connection, planet);
                    return "";
                }

        );

    }

}