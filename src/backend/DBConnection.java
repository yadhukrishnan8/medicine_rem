// DBConnection.java
// Centralized JDBC connection class.
// Edit DB_URL, DB_USER, DB_PASS to match your MySQL setup.
package backend;
import java.sql.*;

public class DBConnection {
    // Update these constants to match your DB
    public static final String DB_URL = "jdbc:mysql://localhost:3306/medicine_system?serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "ykdhu@2005";

    // Load driver (optional with modern JDBC) and return connection
    public static Connection getConnection() throws SQLException {
        // Ensure you have the MySQL JDBC driver JAR on your classpath
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
