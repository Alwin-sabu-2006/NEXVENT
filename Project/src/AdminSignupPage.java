import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class AdminSignupPage {

    private static final String ADMIN_SECRET_CODE = "Admin@1234";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame signupFrame = new JFrame("Create Admin Account");
        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signupFrame.getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel();
        // Reduced rows back to 9 for fields + 1 for buttons = 10 total
        mainPanel.setLayout(new GridLayout(10, 2, 15, 15));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setOpaque(false);

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 14);
        Font fieldFont = new Font("Tahoma", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        final JTextField userField = new JTextField();
        userField.setFont(fieldFont);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        final JPasswordField passField = new JPasswordField();
        passField.setFont(fieldFont);

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(labelFont);
        final JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setFont(fieldFont);

        JLabel secretCodeLabel = new JLabel("Admin Secret Code:");
        secretCodeLabel.setFont(labelFont);
        final JPasswordField secretCodeField = new JPasswordField();
        secretCodeField.setFont(fieldFont);

        JLabel regNoLabel = new JLabel("Staff/Registration No:");
        regNoLabel.setFont(labelFont);
        final JTextField regNoField = new JTextField();
        regNoField.setFont(fieldFont);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        final JTextField emailField = new JTextField();
        emailField.setFont(fieldFont);

        JLabel phoneLabel = new JLabel("Phone Number (10 digits, starts 6-9):");
        phoneLabel.setFont(labelFont);
        final JTextField phoneField = new JTextField();
        phoneField.setFont(fieldFont);

        // Department Label and ComboBox Removed

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(labelFont);
        final JRadioButton maleButton = new JRadioButton("Male");
        maleButton.setFont(fieldFont);
        maleButton.setOpaque(false);
        final JRadioButton femaleButton = new JRadioButton("Female");
        femaleButton.setFont(fieldFont);
        femaleButton.setOpaque(false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setFont(buttonFont);
        signupButton.setBackground(new Color(60, 140, 230));
        signupButton.setForeground(Color.WHITE);
        signupButton.setMargin(new Insets(8, 15, 8, 15));

        JButton backButton = new JButton("Back");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setMargin(new Insets(8, 15, 8, 15));

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());
                String secretCode = new String(secretCodeField.getPassword());
                String regNo = regNoField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                // String dept removed
                String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : null);

                // Validation updated (removed dept check)
                if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || secretCode.isEmpty() ||
                        regNo.isEmpty() || email.isEmpty() || phone.isEmpty() || gender == null) {
                    JOptionPane.showMessageDialog(signupFrame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!password.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(signupFrame, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!secretCode.equals(ADMIN_SECRET_CODE)) {
                    JOptionPane.showMessageDialog(signupFrame, "Incorrect Admin Secret Code.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    JOptionPane.showMessageDialog(signupFrame, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!phone.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(signupFrame, "Please enter exactly 10 digits for the phone number.", "Invalid Phone Length", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                char firstDigit = phone.charAt(0);
                if (firstDigit < '6' || firstDigit > '9') {
                    JOptionPane.showMessageDialog(signupFrame, "Phone number must start with 6, 7, 8, or 9.", "Invalid Phone Start", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet generatedKeys = null;
                try {
                    conn = DatabaseManager.getConnection();
                    if (conn == null) throw new SQLException("Cannot connect to database.");

                    // SQL Updated (removed department column and placeholder)
                    String sql = "INSERT INTO users (username, password, role, reg_no, email, gender, phone) " +
                            "VALUES (?, ?, 'admin', ?, ?, ?, ?)"; // Only 6 placeholders after 'admin'
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, regNo);
                    stmt.setString(4, email);
                    stmt.setString(5, gender);
                    stmt.setString(6, phone); // Set the 6th parameter

                    int rowsInserted = stmt.executeUpdate();

                    if (rowsInserted > 0) {
                        generatedKeys = stmt.getGeneratedKeys();
                        int userId = -1;
                        if (generatedKeys.next()) userId = generatedKeys.getInt(1);
                        if (userId > 0) {
                            Usersession.login(userId, username);
                            JOptionPane.showMessageDialog(signupFrame, "Admin account created! Logging in...", "Success", JOptionPane.INFORMATION_MESSAGE);
                            OrganizerDashboard.createAndShowGUI();
                            signupFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(signupFrame, "Error creating account (could not get ID).", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        JOptionPane.showMessageDialog(signupFrame, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Check if the error is due to the missing department column IF it's NOT NULL in DB
                        if (ex.getMessage().toLowerCase().contains("column 'department' cannot be null")) {
                            JOptionPane.showMessageDialog(signupFrame, "Database Error: Department is required but was removed from form.", "Configuration Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(signupFrame, "Error creating account.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                        ex.printStackTrace();
                    }
                } finally {
                    try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (stmt != null) stmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminAuthPage.createAndShowGUI();
                signupFrame.dispose();
            }
        });

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);
        mainPanel.add(confirmPassLabel);
        mainPanel.add(confirmPassField);
        mainPanel.add(secretCodeLabel);
        mainPanel.add(secretCodeField);
        mainPanel.add(regNoLabel);
        mainPanel.add(regNoField);
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);
        mainPanel.add(phoneLabel);
        mainPanel.add(phoneField);

        mainPanel.add(genderLabel);
        mainPanel.add(genderPanel);

        JPanel buttonHolderPanel = new JPanel(new BorderLayout());
        buttonHolderPanel.setOpaque(false);
        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonFlowPanel.setOpaque(false);
        buttonFlowPanel.add(backButton);
        buttonFlowPanel.add(signupButton);
        buttonHolderPanel.add(buttonFlowPanel, BorderLayout.CENTER);


        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel(""));
        mainPanel.add(buttonHolderPanel);

        signupFrame.add(mainPanel);
        signupFrame.pack();
        signupFrame.setLocationRelativeTo(null);
        signupFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}