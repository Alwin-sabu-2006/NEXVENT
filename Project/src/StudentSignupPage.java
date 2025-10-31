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

public class StudentSignupPage {

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

        final JFrame signupFrame = new JFrame("Create Student Account");

        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signupFrame.getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(13, 2, 15, 15));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setOpaque(false);

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 14);
        Font fieldFont = new Font("Times New Roman", Font.PLAIN, 14);
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

        JLabel regNoLabel = new JLabel("Registration Number:");
        regNoLabel.setFont(labelFont);
        final JTextField regNoField = new JTextField();
        regNoField.setFont(fieldFont);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        final JTextField emailField = new JTextField();
        emailField.setFont(fieldFont);

        JLabel phoneLabel = new JLabel("Phone Number (10 digits):");
        phoneLabel.setFont(labelFont);
        final JTextField phoneField = new JTextField();
        phoneField.setFont(fieldFont);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(labelFont);
        String[] departments = {"Computer Science And Engineering","Computer Science with AI","Artificial Intelligence and DataScience" ,"Electronics And Communication","Mechanical Engineering", "Electrical Engineering", "Civil", "Electronics", "MCA", "Other"};
        final JComboBox<String> deptBox = new JComboBox<>(departments);
        deptBox.setFont(fieldFont);

        JLabel batchLabel = new JLabel("Batch:");
        batchLabel.setFont(labelFont);
        String[] batches = {"A","B","C", "N/A"};
        final JComboBox<String> batchBox = new JComboBox<>(batches);
        batchBox.setFont(fieldFont);

        JLabel semLabel = new JLabel("Semester:");
        semLabel.setFont(labelFont);
        String[] sem = {"1","2","3","4","5","6","7","8", "N/A"};
        final JComboBox<String> semBox = new JComboBox<>(sem);
        semBox.setFont(fieldFont);

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
                String regNo = regNoField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String dept = (String) deptBox.getSelectedItem();
                String batch = (String) batchBox.getSelectedItem();
                String semester = (String) semBox.getSelectedItem();
                String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : null);

                if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty() ||
                        regNo.isEmpty() || email.isEmpty() || phone.isEmpty() || dept == null || batch == null || semester == null || gender == null) {
                    JOptionPane.showMessageDialog(signupFrame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!password.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(signupFrame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    JOptionPane.showMessageDialog(signupFrame, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!phone.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(signupFrame, "Please enter a valid 10-digit phone number.", "Invalid Phone", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet generatedKeys = null;
                try {
                    conn = DatabaseManager.getConnection();
                    if (conn == null) throw new SQLException("Cannot connect to database.");


                    String sql = "INSERT INTO users (username, password, role, reg_no, email, gender, phone, department, batch, sem) " +
                            "VALUES (?, ?, 'student', ?, ?, ?, ?, ?, ?, ?)";
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, regNo);
                    stmt.setString(4, email);
                    stmt.setString(5, gender);
                    stmt.setString(6, phone);
                    stmt.setString(7, dept);
                    stmt.setString(8, batch);
                    stmt.setString(9, semester);

                    int rowsInserted = stmt.executeUpdate();

                    if (rowsInserted > 0) {
                        generatedKeys = stmt.getGeneratedKeys();
                        int userId = -1;
                        if (generatedKeys.next()) userId = generatedKeys.getInt(1);
                        if (userId > 0) {
                            Usersession.login(userId, username); // RELIES ON "Usersession.java"
                            JOptionPane.showMessageDialog(signupFrame, "Student account created! Logging in...", "Success", JOptionPane.INFORMATION_MESSAGE);
                            StudentDashboard.createAndShowGUI(); // RELIES ON "StudentDashboard.java"
                            signupFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(signupFrame, "Error creating account (could not get ID).", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        JOptionPane.showMessageDialog(signupFrame, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(signupFrame, "Error creating account.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
                StudentAuthPage.createAndShowGUI();
                signupFrame.dispose();
            }
        });


        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);
        mainPanel.add(confirmPassLabel);
        mainPanel.add(confirmPassField);
        mainPanel.add(regNoLabel);
        mainPanel.add(regNoField);
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);
        mainPanel.add(phoneLabel);
        mainPanel.add(phoneField);
        mainPanel.add(deptLabel);
        mainPanel.add(deptBox);
        mainPanel.add(batchLabel);
        mainPanel.add(batchBox);
        mainPanel.add(semLabel);
        mainPanel.add(semBox);
        mainPanel.add(genderLabel);
        mainPanel.add(genderPanel);


        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonFlowPanel.setOpaque(false);
        buttonFlowPanel.add(backButton);
        buttonFlowPanel.add(signupButton);


        mainPanel.add(new JLabel(""));

        mainPanel.add(buttonFlowPanel);


        mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel(""));


        signupFrame.add(mainPanel);


        signupFrame.pack();

        signupFrame.setLocationRelativeTo(null);
        signupFrame.setVisible(true);
    }
}