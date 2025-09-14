// PatientFrame.java
// Top-level window for patient. Contains tabs for features.

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class PatientFrame extends JFrame {
    private int patientUserId;
    private String patientName;
    private String hospitalId;

    public PatientFrame(int userId, String name, String hospitalId) {
        this.patientUserId = userId;
        this.patientName = name;
        this.hospitalId = hospitalId;
        setTitle("Patient - " + patientName + " (" + hospitalId + ")");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // Dashboard simple panel
        JPanel dashboard = PatientPanels.createDashboardPanel(patientUserId, patientName);
        tabs.addTab("Dashboard", dashboard);

        // Medicine Schedule tab (read-only)
        JPanel schedule = PatientPanels.createSchedulePanel(patientUserId);
        tabs.addTab("Medicine Schedule", schedule);

        // Health Data Tracker
        JPanel tracker = PatientPanels.createHealthTrackerPanel(patientUserId);
        tabs.addTab("Health Tracker", tracker);

        // Lab Report Upload
        JPanel upload = PatientPanels.createLabUploadPanel(patientUserId);
        tabs.addTab("Lab Reports", upload);

        // Appointment booking
        JPanel appt = PatientPanels.createAppointmentPanel(patientUserId);
        tabs.addTab("Appointments", appt);

        getContentPane().add(tabs, BorderLayout.CENTER);

        // Logout button
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        getContentPane().add(logout, BorderLayout.SOUTH);
    }
}
