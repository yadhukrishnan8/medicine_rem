// LoginFrame.java
// Simple Swing login screen for both doctors and patients.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private JButton loginBtn;

    public LoginFrame() {
        setTitle("Medicine System - Login");
        setSize(380, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JPanel panel = new JPanel(new GridLayout(4,1,6,6));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        idField = new JTextField();
        passField = new JPasswordField();

        panel.add(new JLabel("Hospital/Doctor ID:"));
        panel.add(idField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());

        JPanel bottom = new JPanel();
        bottom.add(loginBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    private void doLogin() {
        String hid = idField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        if (hid.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both ID and password", "Input required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Authenticate: hospital_id + password
        // We need to find user_id with credentials.
        int userId = AuthService.authenticate(hid, pass);
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "Login failed. Check credentials.", "Auth Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = AuthService.getRole(userId);
        String[] info = AuthService.getUserInfo(userId); // hospital_id, name

        // Open appropriate frame
        if ("patient".equalsIgnoreCase(role)) {
            PatientFrame pf = new PatientFrame(userId, info[1], info[0]); // pass numeric id, name, hospital id string
            pf.setVisible(true);
            this.dispose();
        } else if ("doctor".equalsIgnoreCase(role)) {
            DoctorFrame df = new DoctorFrame(userId, info[1], info[0]);
            df.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Unknown role", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
