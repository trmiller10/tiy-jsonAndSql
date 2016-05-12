import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;

public class Main {

    public static void main(String[] args){

        Spark.get(
                "/planet",
                (request, response) -> {
                    Planet planet = new Planet();
                    planet.name = "Earth";
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