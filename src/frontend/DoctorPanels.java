// DoctorPanels.java
// Panels for the doctor view.

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class DoctorPanels {

    public static JPanel createDashboardPanel(int doctorId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JLabel lbl = new JLabel("Assigned Patients");
        p.add(lbl, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        List<String> patients = DoctorService.getAssignedPatients(doctorId);
        for (String s : patients) model.addElement(s);

        JList<String> list = new JList<>(model);
        p.add(new JScrollPane(list), BorderLayout.CENTER);

        return p;
    }

    public static JPanel createPatientManagementPanel(int doctorId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel(new GridLayout(1,2,6,6));
        JTextField patientIdField = new JTextField();
        top.add(new JLabel("Patient user_id:"));
        top.add(patientIdField);

        JPanel form = new JPanel(new GridLayout(5,2,6,6));
        JTextField name = new JTextField();
        JTextField dosage = new JTextField();
        JTextField frequency = new JTextField();
        JTextField time = new JTextField("HH:MM");

        form.add(new JLabel("Medicine Name:")); form.add(name);
        form.add(new JLabel("Dosage:")); form.add(dosage);
        form.add(new JLabel("Frequency:")); form.add(frequency);
        form.add(new JLabel("Time (HH:MM):")); form.add(time);

        JButton add = new JButton("Add Medicine");
        add.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientIdField.getText().trim());
                String nm = name.getText().trim();
                String d = dosage.getText().trim();
                String f = frequency.getText().trim();
                String t = time.getText().trim();
                Time ti = Time.valueOf(t + ":00");
                boolean ok = DoctorService.addMedicine(pid, doctorId, nm, d, f, ti);
                JOptionPane.showMessageDialog(p, ok ? "Added" : "Failed to add");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Error: check inputs (patient id and time format HH:MM)");
            }
        });

        JPanel bottom = new JPanel(); bottom.add(add);

        p.add(top, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel createHealthReviewPanel(int doctorId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel(new FlowLayout());
        JTextField patientId = new JTextField(6);
        JButton load = new JButton("Load Health Data");
        top.add(new JLabel("Patient user_id:")); top.add(patientId); top.add(load);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        load.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                List<String> data = DoctorService.getHealthData(pid);
                area.setText("");
                data.forEach(d -> area.append(d + "\n"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Enter valid patient id");
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    public static JPanel createLabReviewPanel(int doctorId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel();
        JTextField patientId = new JTextField(6);
        JButton load = new JButton("Load Reports");
        top.add(new JLabel("Patient user_id:")); top.add(patientId); top.add(load);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        JScrollPane sc = new JScrollPane(list);

        JButton review = new JButton("Mark Reviewed (enter ReportID and Comment)");
        review.addActionListener(e -> {
            String ridStr = JOptionPane.showInputDialog(p, "Enter ReportID to mark reviewed:");
            if (ridStr == null) return;
            try {
                int rid = Integer.parseInt(ridStr.trim());
                String comment = JOptionPane.showInputDialog(p, "Comment (optional):");
                boolean ok = DoctorService.reviewLabReport(rid, comment == null ? "" : comment);
                JOptionPane.showMessageDialog(p, ok ? "Marked reviewed" : "Failed");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Invalid ReportID");
            }
        });

        load.addActionListener(e -> {
            model.clear();
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                List<String> reports = DoctorService.getLabReports(pid);
                for (String r : reports) model.addElement(r);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Enter valid patient id");
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(sc, BorderLayout.CENTER);
        p.add(review, BorderLayout.SOUTH);
        return p;
    }

    public static JPanel createAppointmentPanel(int doctorId) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JButton refresh = new JButton("Refresh Appointments");
        refresh.addActionListener(e -> {
            List<String> appts = AppointmentService.getPendingAppointments(doctorId);
            area.setText("");
            appts.forEach(a -> area.append(a + "\n"));
        });

        JButton confirm = new JButton("Confirm Appointment (enter ApptID)");
        confirm.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(p, "Enter Appointment ID to confirm:");
            if (s == null) return;
            try {
                int id = Integer.parseInt(s.trim());
                boolean ok = DoctorService.confirmAppointment(id);
                JOptionPane.showMessageDialog(p, ok ? "Confirmed" : "Failed");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Invalid ID");
            }
        });

        JPanel top = new JPanel();
        top.add(refresh); top.add(confirm);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }
}
