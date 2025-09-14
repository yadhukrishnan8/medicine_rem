// PatientPanels.java
// Static methods to create Swing panels used in PatientFrame.

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PatientPanels {

    // Dashboard panel: shows today's reminders (we show schedule) and quick actions
    public static JPanel createDashboardPanel(int patientId, String patientName) {
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new GridLayout(2,1));
        top.add(new JLabel("Welcome, " + patientName));
        top.add(new JLabel("Assigned Doctor: (see profile)"));

        JTextArea reminders = new JTextArea();
        reminders.setEditable(false);
        List<String> meds = PatientService.getMedicineSchedule(patientId);
        StringBuilder sb = new StringBuilder("Today's Medicines:\n");
        if (meds.isEmpty()) sb.append("No medicines prescribed.\n");
        else {
            for (String m : meds) sb.append(m).append("\n");
        }
        reminders.setText(sb.toString());

        // Quick actions
        JPanel actions = new JPanel();
        JButton sendDoubt = new JButton("Send Doubt/Symptom");
        sendDoubt.addActionListener(e -> {
            String msg = JOptionPane.showInputDialog(p, "Describe your doubt/symptoms:");
            if (msg != null && !msg.trim().isEmpty()) {
                boolean ok = PatientService.sendDoubtToDoctor(patientId, msg.trim());
                JOptionPane.showMessageDialog(p, ok ? "Sent to doctor" : "Failed to send");
            }
        });
        actions.add(sendDoubt);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(reminders), BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel createSchedulePanel(int patientId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        DefaultListModel<String> model = new DefaultListModel<>();
        List<String> meds = PatientService.getMedicineSchedule(patientId);
        for (String m : meds) model.addElement(m);
        JList<String> list = new JList<>(model);
        p.add(new JLabel("Prescribed Medicines (Read-only)"), BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    public static JPanel createHealthTrackerPanel(int patientId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel form = new JPanel(new GridLayout(6,2,6,6));
        JTextField bp = new JTextField();
        JTextField sugar = new JTextField();
        JTextField weight = new JTextField();
        JTextArea symptoms = new JTextArea(3,20);

        form.add(new JLabel("Blood Pressure (e.g., 120/80):")); form.add(bp);
        form.add(new JLabel("Sugar (mg/dL):")); form.add(sugar);
        form.add(new JLabel("Weight (kg):")); form.add(weight);
        form.add(new JLabel("Symptoms:")); form.add(new JScrollPane(symptoms));

        JButton save = new JButton("Save Record");
        save.addActionListener(e -> {
            String bpv = bp.getText().trim();
            String sugarv = sugar.getText().trim();
            String w = weight.getText().trim();
            String s = symptoms.getText().trim();
            boolean ok = PatientService.saveHealthData(patientId, bpv, sugarv, w, s, Date.valueOf(LocalDate.now()));
            JOptionPane.showMessageDialog(p, ok ? "Saved health data" : "Failed to save");
        });

        JButton viewHistory = new JButton("View History");
        viewHistory.addActionListener(e -> {
            List<String> data = DoctorService.getHealthData(patientId); // reuse doctor method
            JTextArea area = new JTextArea();
            data.forEach(d -> area.append(d + "\n"));
            area.setEditable(false);
            JOptionPane.showMessageDialog(p, new JScrollPane(area), "Health History", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel buttons = new JPanel(); buttons.add(save); buttons.add(viewHistory);

        p.add(form, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel createLabUploadPanel(int patientId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel();
        JTextField fileField = new JTextField(30);
        JButton choose = new JButton("Choose File");
        choose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int rc = chooser.showOpenDialog(p);
            if (rc == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                fileField.setText(f.getAbsolutePath());
            }
        });
        top.add(fileField); top.add(choose);

        JButton upload = new JButton("Upload");
        upload.addActionListener(e -> {
            String fp = fileField.getText().trim();
            if (fp.isEmpty()) {
                JOptionPane.showMessageDialog(p, "Choose file path first.");
                return;
            }
            boolean ok = PatientService.uploadLabReport(patientId, fp);
            JOptionPane.showMessageDialog(p, ok ? "Uploaded (pending review)" : "Upload failed");
        });

        JButton view = new JButton("View Uploads");
        view.addActionListener(e -> {
            List<String> reports = DoctorService.getLabReports(patientId);
            JTextArea area = new JTextArea();
            reports.forEach(r -> area.append(r + "\n"));
            area.setEditable(false);
            JOptionPane.showMessageDialog(p, new JScrollPane(area), "My Reports", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btns = new JPanel(); btns.add(upload); btns.add(view);
        p.add(new JLabel("Upload lab PDFs/images (we store path)"), BorderLayout.NORTH);
        p.add(top, BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel createAppointmentPanel(int patientId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel form = new JPanel(new GridLayout(3,2,6,6));
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField timeField = new JTextField("HH:MM");

        form.add(new JLabel("Preferred Date:")); form.add(dateField);
        form.add(new JLabel("Preferred Time (HH:MM):")); form.add(timeField);

        JButton request = new JButton("Request Appointment");
        request.addActionListener(e -> {
            try {
                String ds = dateField.getText().trim();
                String ts = timeField.getText().trim();
                Date d = Date.valueOf(ds);
                Time t = Time.valueOf(ts + ":00");
                // find assigned doctor_id
                String sql = "SELECT assigned_doctor_id FROM users WHERE user_id = ?";
                try (java.sql.Connection conn = DBConnection.getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, patientId);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int doctorId = rs.getInt("assigned_doctor_id");
                            boolean ok = PatientService.requestAppointment(patientId, doctorId, d, t);
                            JOptionPane.showMessageDialog(p, ok ? "Appointment requested" : "Failed");
                        } else {
                            JOptionPane.showMessageDialog(p, "No assigned doctor found");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(p, "Error requesting appointment: " + ex.getMessage());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Invalid date/time format. Use YYYY-MM-DD and HH:MM");
            }
        });

        JButton viewRequests = new JButton("View Requests");
        viewRequests.addActionListener(e -> {
            try {
                String sql = "SELECT appt_id, appt_date, appt_time, status FROM appointments WHERE patient_id = ? ORDER BY created_at DESC";
                try (java.sql.Connection conn = DBConnection.getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, patientId);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append("ID:").append(rs.getInt("appt_id")).append(" | Date:").append(rs.getDate("appt_date"))
                                    .append(" | Time:").append(rs.getTime("appt_time")).append(" | ").append(rs.getString("status")).append("\n");
                        }
                        JTextArea area = new JTextArea(sb.toString());
                        area.setEditable(false);
                        JOptionPane.showMessageDialog(p, new JScrollPane(area), "My Appointments", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel btns = new JPanel();
        btns.add(request);
        btns.add(viewRequests);

        p.add(form, BorderLayout.NORTH);
        p.add(btns, BorderLayout.CENTER);
        return p;
    }
}
