import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginPage {

    private static final String ADMIN_SECRET_CODE = "Admin@1234";

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame adminFrame = new JFrame("Admin Login");
        adminFrame.setSize(400, 250);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        final JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        final JPasswordField passField = new JPasswordField();

        JLabel secretCodeLabel = new JLabel("Admin Secret Code:");
        final JPasswordField secretCodeField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String secretCode = new String(secretCodeField.getPassword());

                if (!secretCode.equals(ADMIN_SECRET_CODE)) {
                    JOptionPane.showMessageDialog(adminFrame, "Incorrect Admin Secret Code.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "SELECT user_id, username FROM users WHERE username = ? AND password = ? AND role = 'admin'";
                Connection conn = DatabaseManager.getConnection();

                if (conn == null) {
                    JOptionPane.showMessageDialog(adminFrame, "Could not connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt("user_id");
                                String foundUsername = rs.getString("username");
                            Usersession.login(userId, foundUsername);

                            System.out.println("Admin login successful! UserID: " + userId);
                            OrganizerDashboard.createAndShowGUI();
                            adminFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(adminFrame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminFrame, "Error during login.", "Database Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminAuthPage.createAndShowGUI();
                adminFrame.dispose();
            }
        });

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);
        mainPanel.add(secretCodeLabel);
        mainPanel.add(secretCodeField);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.add(backButton);

        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loginPanel.add(loginButton);

        mainPanel.add(backPanel);
        mainPanel.add(loginPanel);

        adminFrame.add(mainPanel);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}