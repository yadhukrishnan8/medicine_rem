// PatientService.java
// Methods used by patient UI: view schedule, insert health data, upload report, book appointment, send doubts.
package backend;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    // Get medicine schedule for a patient
    public static List<String> getMedicineSchedule(int patientId) {
        String sql = "SELECT name, dosage, frequency, time_of_day FROM medicines WHERE patient_id = ?";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("name") + " | " + rs.getString("dosage")
                            + " | " + rs.getString("frequency") + " | " + rs.getTime("time_of_day");
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Insert health data record
    public static boolean saveHealthData(int patientId, String bp, String sugar, String weight, String symptoms, Date date) {
        String sql = "INSERT INTO health_data (patient_id, bp, sugar, weight, symptoms, record_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, bp);
            ps.setString(3, sugar);
            ps.setString(4, weight);
            ps.setString(5, symptoms);
            ps.setDate(6, date);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Upload lab report entry (we store file path)
    public static boolean uploadLabReport(int patientId, String filePath) {
        String sql = "INSERT INTO lab_reports (patient_id, file_path, status) VALUES (?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, filePath);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Request appointment
    public static boolean requestAppointment(int patientId, int doctorId, Date date, Time time) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appt_date, appt_time, status) VALUES (?, ?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setDate(3, date);
            ps.setTime(4, time);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Send doubt/symptom as a doctor_note with doctor_id = assigned doctor
    public static boolean sendDoubtToDoctor(int patientId, String message) {
        String findDoctorSql = "SELECT assigned_doctor_id FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findDoctorSql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int doctorId = rs.getInt("assigned_doctor_id");
                    if (doctorId <= 0) return false;
                    String insertSql = "INSERT INTO doctor_notes (doctor_id, patient_id, message) VALUES (?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                        ps2.setInt(1, doctorId);
                        ps2.setInt(2, patientId);
                        ps2.setString(3, message);
                        int r = ps2.executeUpdate();
                        return r == 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
