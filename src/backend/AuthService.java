// AuthService.java
// Handles authentication and basic user lookup.
package backend;
import java.sql.*;

public class AuthService {

    // Authenticate by hospital_id and password
    // Returns user_id if successful, else -1
    public static int authenticate(String hospitalId, String password) {
        String sql = "SELECT user_id FROM users WHERE hospital_id = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospitalId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Get role by user_id
    public static String getRole(int userId) {
        String sql = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get hospital id string and name
    public static String[] getUserInfo(int userId) {
        String sql = "SELECT hospital_id, name FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("hospital_id"), rs.getString("name")};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"", ""};
    }
}
