import javax.swing.*;  // Import Swing components
import java.awt.*;      // Import AWT components
import java.awt.event.*; // Import event handling classes
import java.sql.*;
public class LoginApp extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/softwaretesting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345678";

    public LoginApp(boolean isTesting) {
        if (!isTesting) {
            setTitle("Login Screen");
            setSize(350, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2, 10, 10));

            // Email Label and Text Field
            panel.add(new JLabel("Email:"));
            emailField = new JTextField();
            panel.add(emailField);

            // Password Label and Password Field
            panel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            panel.add(passwordField);

            // Login Button
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(new LoginAction());
            panel.add(loginButton);

            add(panel);
        }
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword()); // Password is ignored for validation

            String userName = authenticateUser(email);
            if (userName != null) {
                JOptionPane.showMessageDialog(null, "Welcome, " + userName + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "User not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String authenticateUser(String email) {
        String userName = null;
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return null;  // Invalid email
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT name FROM User WHERE Email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userName = rs.getString("Name");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userName;
    }

    public static void main(String[] args) {
        // Run GUI only if not in a headless environment
        SwingUtilities.invokeLater(() -> {
            boolean isTesting = System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless").equals("true");
            LoginApp loginApp = new LoginApp(isTesting);
            System.out.println("test4");
            loginApp.setVisible(true);
        });
    }
}
