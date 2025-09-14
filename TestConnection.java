// Ensure the backend.DBConnection class exists and is correctly referenced
import backend.DBConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Database connection successful!");
            } else {
                System.out.println("❌ Connection returned null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
