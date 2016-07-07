public class Moon {

    int id;
    String name;
    String color;
    int planet_id;

    public Moon(){}

    public Moon(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Moon(String name, String color, int planet_id){
        this.name = name;
        this.color = color;
        this.planet_id = planet_id;
    }

    public Moon(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Moon(int id, String name, String color, int planet_id) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.planet_id = planet_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getplanet_id() {
        return planet_id;
    }

    public void setplanet_id(int planet_id) {
        this.planet_id = planet_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
