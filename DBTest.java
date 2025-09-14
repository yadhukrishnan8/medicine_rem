import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/medicine_system"; // database name
        String user = "root"; // your MySQL username
        String password = "ykdhu@2005"; // your MySQL password

        try {
            // 1. Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Connect to database
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connected successfully!");

            // 3. Run a simple query
            String sql = "SELECT * FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\nUsers in database:");
            while (rs.next()) {
                System.out.println(
                        rs.getString("hospital_id") + " | "
                        + rs.getString("name") + " | "
                        + rs.getString("role"));
            }

            // Close connection
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
