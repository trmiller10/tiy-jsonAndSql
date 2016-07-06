public class Moon {

    int id;
    String name;
    String color;
    int planetId;

    public Moon(){}

    public Moon(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Moon(String name, String color, int planetId){
        this.name = name;
        this.color = color;
        this.planetId = planetId;
    }

    public Moon(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanetId() {
        return planetId;
    }

    public void setPlanetId(int planetId) {
        this.planetId = planetId;
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
