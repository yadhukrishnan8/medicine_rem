// Main.java
// Entry point for the application.

public class Main {
    public static void main(String[] args) {
        // Launch Login Frame (Swing)
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
