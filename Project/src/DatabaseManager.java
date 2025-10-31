import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_NAME = "eventparticipationmanager";

    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER_CLASS);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!");

        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found!");
            System.out.println("Make sure you added the .jar file to your project libraries.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Database connection failed!");
            System.out.println("Check your URL, username, password, and if XAMPP MySQL is running.");
            e.printStackTrace();
        }

        return connection;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();

        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.out.println("Error while closing the connection.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to get a database connection.");
        }
    }
}