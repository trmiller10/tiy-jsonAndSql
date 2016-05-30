import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Taylor on 5/30/16.
 */
public class PlanetsService {

    private final Connection connection;

    public PlanetsService(Connection connection) {
        this.connection = connection;
    }

    public void initDatabase() throws SQLException {

        Statement statement = connection.createStatement();

        statement.execute("DROP TABLE  IF EXISTS planets");

        statement.execute("DROP TABLE  IF EXISTS moons");

        statement.execute("CREATE TABLE IF NOT EXISTS planets (id IDENTITY, name VARCHAR, radius INT, supportsLife BOOLEAN, distanceFromSun DOUBLE, moons OTHER)");

        statement.execute("CREATE TABLE IF NOT EXISTS moons (id IDENTITY, name VARCHAR, color VARCHAR)");
    }
}

