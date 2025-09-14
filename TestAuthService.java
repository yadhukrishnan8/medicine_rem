import backend.AuthService;

public class TestAuthService {
    public static void main(String[] args) {
        // Test the authenticate method
        int userId = AuthService.authenticate("test_hospital_id", "test_password");
        if (userId != -1) {
            System.out.println("✅ Authentication successful! User ID: " + userId);
        } else {
            System.out.println("❌ Authentication failed!");
        }

        // Test the getRole method
        String role = AuthService.getRole(userId);
        if (role != null) {
            System.out.println("✅ Role fetched successfully! Role: " + role);
        } else {
            System.out.println("❌ Failed to fetch role!");
        }

        // Test the getUserInfo method
        String[] userInfo = AuthService.getUserInfo(userId);
        if (!userInfo[0].isEmpty() && !userInfo[1].isEmpty()) {
            System.out.println("✅ User info fetched successfully! Hospital ID: " + userInfo[0] + ", Name: " + userInfo[1]);
        } else {
            System.out.println("❌ Failed to fetch user info!");
        }
    }
}