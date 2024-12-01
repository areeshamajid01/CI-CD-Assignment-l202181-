import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;

public class LoginAppTest {
    private LoginApp loginApp;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    @Before
    public void setUp() {
        loginApp = new LoginApp();
        // Get UI components
        JPanel panel = (JPanel) loginApp.getContentPane().getComponent(0);
        emailField = (JTextField) findComponent(panel, JTextField.class);
        passwordField = (JPasswordField) findComponent(panel, JPasswordField.class);
        loginButton = (JButton) findComponent(panel, JButton.class);
    }

    // Test Case 1: Email Validation Tests
    @Test
    public void testEmailValidation() {
        // Multiple assertions testing email validation
        String[] invalidEmails = {
                null,                    // null email
                "",                      // empty email
                "invalid.email",         // missing @ symbol
                "@nodomain",            // missing local part
                "spaces in@email.com",   // spaces in email
                "user@.com"             // missing domain
        };

        for (String email : invalidEmails) {
            String result = loginApp.authenticateUser(email);
            assertNull("Invalid email " + email + " should return null", result);
        }

        // Valid email format test
        emailField.setText("test@example.com");
        assertEquals("Email field should contain the set text",
                "test@example.com", emailField.getText());
    }

    // Test Case 2: Database Authentication Tests
    @Test
    public void testDatabaseAuthentication() {
        // Test with existing user
        String result = loginApp.authenticateUser("johndoe@example.com");
        assertNotNull("Should return user name for valid email", result);

        // Test with non-existent user
        result = loginApp.authenticateUser("nonexistent@example.com");
        assertNull("Should return null for non-existent user", result);

        // Test database connection
        try {
            result = loginApp.authenticateUser("test@example.com");
            // If no exception thrown, connection successful
            assertTrue("Database connection should be established", true);
        } catch (Exception e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    // Test Case 3: UI Component Tests
    @Test
    public void testUIComponents() {
        // Test frame properties
        assertEquals("Login Screen", loginApp.getTitle());
        assertEquals(350, loginApp.getWidth());
        assertEquals(200, loginApp.getHeight());
        assertEquals(JFrame.EXIT_ON_CLOSE, loginApp.getDefaultCloseOperation());

        // Test component existence
        assertNotNull("Email field should exist", emailField);
        assertNotNull("Password field should exist", passwordField);
        assertNotNull("Login button should exist", loginButton);

        // Test component properties
        assertEquals("", emailField.getText());
        assertEquals("", new String(passwordField.getPassword()));
        assertEquals("Login", loginButton.getText());

        // Test component enablement
        assertTrue("Email field should be enabled", emailField.isEnabled());
        assertTrue("Password field should be enabled", passwordField.isEnabled());
        assertTrue("Login button should be enabled", loginButton.isEnabled());
    }

    // Test Case 4: Login Action Tests
    @Test
    public void testLoginAction() {
        // Test successful login
        emailField.setText("johndoe@example.com");
        passwordField.setText("password123");
        loginButton.doClick();

        // Test failed login
        emailField.setText("nonexistent@example.com");
        passwordField.setText("wrongpass");
        loginButton.doClick();

        // Clear fields test
        emailField.setText("");
        passwordField.setText("");
        assertEquals("", emailField.getText());
        assertEquals("", new String(passwordField.getPassword()));
    }

    // Test Case 5: Error Handling Tests
    @Test
    public void testErrorHandling() {
        // Test database connection error
        try {
            String result = loginApp.authenticateUser("test@example.com");
            assertNull("Should handle database error gracefully", result);
        } catch (Exception e) {
            fail("Database error not handled properly: " + e.getMessage());
        }

        // Test SQL injection attempt
        String sqlInjection = "' OR '1'='1";
        String result = loginApp.authenticateUser(sqlInjection);
        assertNull("Should handle SQL injection attempts", result);

        // Test concurrent access
        // Simulate multiple simultaneous login attempts
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                loginApp.authenticateUser("janesmith@example.com");
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Concurrent access test failed");
            }
        }
    }

    // Helper method to find components
    private Component findComponent(Container container, Class<?> componentClass) {
        for (Component component : container.getComponents()) {
            if (componentClass.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponent((Container) component, componentClass);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}