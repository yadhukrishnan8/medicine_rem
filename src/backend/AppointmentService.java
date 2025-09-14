// AppointmentService.java
// Helpers to fetch appointment requests for doctor.
package backend;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    // Get pending appointments for a doctor
    public static List<String> getPendingAppointments(int doctorId) {
        String sql = "SELECT appt_id, patient_id, appt_date, appt_time, status FROM appointments WHERE doctor_id = ? ORDER BY created_at DESC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = "ApptID:" + rs.getInt("appt_id") + " | PatientID:" + rs.getInt("patient_id")
                            + " | Date:" + rs.getDate("appt_date") + " | Time:" + rs.getTime("appt_time") + " | " + rs.getString("status");
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
