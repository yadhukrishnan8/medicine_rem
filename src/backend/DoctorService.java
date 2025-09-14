// DoctorService.java
// Methods used by doctor UI: list patients, manage medicines, review health data and lab reports, leave notes, confirm appointments.
package backend;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    // List assigned patients for a given doctor (returns "hospital_id | name | user_id")
    public static List<String> getAssignedPatients(int doctorId) {
        String sql = "SELECT user_id, hospital_id, name FROM users WHERE assigned_doctor_id = ?";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = rs.getString("hospital_id") + " | " + rs.getString("name") + " | id:" + rs.getInt("user_id");
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a medicine for a patient
    public static boolean addMedicine(int patientId, int doctorId, String name, String dosage, String frequency, Time timeOfDay) {
        String sql = "INSERT INTO medicines (patient_id, doctor_id, name, dosage, frequency, time_of_day) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, name);
            ps.setString(4, dosage);
            ps.setString(5, frequency);
            ps.setTime(6, timeOfDay);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get health data entries for a patient
    public static List<String> getHealthData(int patientId) {
        String sql = "SELECT record_date, bp, sugar, weight, symptoms FROM health_data WHERE patient_id = ? ORDER BY record_date DESC LIMIT 50";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = rs.getDate("record_date") + " | BP:" + rs.getString("bp") + " | Sugar:" + rs.getString("sugar")
                            + " | Weight:" + rs.getString("weight") + " | Symp:" + rs.getString("symptoms");
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // List lab reports for a patient
    public static List<String> getLabReports(int patientId) {
        String sql = "SELECT report_id, file_path, status, uploaded_at FROM lab_reports WHERE patient_id = ?";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = "ReportID:" + rs.getInt("report_id") + " | " + rs.getString("file_path") + " | " + rs.getString("status")
                            + " | " + rs.getTimestamp("uploaded_at");
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Mark a lab report as reviewed and add comment
    public static boolean reviewLabReport(int reportId, String comment) {
        String sql = "UPDATE lab_reports SET status='Reviewed', doctor_comments=? WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment);
            ps.setInt(2, reportId);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Confirm an appointment
    public static boolean confirmAppointment(int apptId) {
        String sql = "UPDATE appointments SET status='Confirmed' WHERE appt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, apptId);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Write a doctor note for a patient
    public static boolean writeNote(int doctorId, int patientId, String message) {
        String sql = "INSERT INTO doctor_notes (doctor_id, patient_id, message) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            ps.setString(3, message);
            int r = ps.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
