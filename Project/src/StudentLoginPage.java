import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentLoginPage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame studentFrame = new JFrame("Student Login");
        studentFrame.setSize(400, 200);
        studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        final JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        final JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                String sql = "SELECT user_id, username FROM users WHERE username = ? AND password = ? AND role = 'student'";

                Connection conn = DatabaseManager.getConnection();

                if (conn == null) {
                    JOptionPane.showMessageDialog(studentFrame,
                            "Could not connect to the database. Check console.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
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

                            System.out.println("Student login successful! UserID: " + userId);
                            StudentDashboard.createAndShowGUI();
                            studentFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(studentFrame,
                                    "Invalid username or password.",
                                    "Login Failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } catch (SQLException ex) {
                    System.out.println("Database error during login.");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(studentFrame,
                            "Error during login.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
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
                StudentAuthPage.createAndShowGUI();
                studentFrame.dispose();
            }
        });

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.add(backButton);

        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loginPanel.add(loginButton);

        mainPanel.add(backPanel);
        mainPanel.add(loginPanel);

        studentFrame.add(mainPanel);
        studentFrame.setLocationRelativeTo(null);
        studentFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}