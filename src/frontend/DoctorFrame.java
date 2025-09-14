// DoctorFrame.java
// Top-level window for doctor. Contains tabs for features.

import javax.swing.*;
import java.awt.*;

public class DoctorFrame extends JFrame {
    private int doctorUserId;
    private String doctorName;
    private String hospitalId;

    public DoctorFrame(int userId, String name, String hospitalId) {
        this.doctorUserId = userId;
        this.doctorName = name;
        this.hospitalId = hospitalId;
        setTitle("Doctor - " + doctorName + " (" + hospitalId + ")");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        JPanel dashboard = DoctorPanels.createDashboardPanel(doctorUserId);
        tabs.addTab("Dashboard", dashboard);

        JPanel patientMgmt = DoctorPanels.createPatientManagementPanel(doctorUserId);
        tabs.addTab("Patient Management", patientMgmt);

        JPanel healthReview = DoctorPanels.createHealthReviewPanel(doctorUserId);
        tabs.addTab("Health Review", healthReview);

        JPanel labReview = DoctorPanels.createLabReviewPanel(doctorUserId);
        tabs.addTab("Lab Reports", labReview);

        JPanel appointment = DoctorPanels.createAppointmentPanel(doctorUserId);
        tabs.addTab("Appointments", appointment);

        getContentPane().add(tabs, BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        getContentPane().add(logout, BorderLayout.SOUTH);
    }
}
