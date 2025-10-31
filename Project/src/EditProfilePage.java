import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditProfilePage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame editFrame = new JFrame("Edit Profile");
        editFrame.setSize(450, 450);
        editFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(7, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Name:");
        final JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        final JTextField emailField = new JTextField();

        JLabel genderLabel = new JLabel("Gender:");
        final JRadioButton maleButton = new JRadioButton("Male");
        final JRadioButton femaleButton = new JRadioButton("Female");
        final ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        JLabel regNoLabel = new JLabel("Registration Number:");
        final JTextField regNoField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone Number:");
        final JTextField phoneField = new JTextField();

        JLabel deptLabel = new JLabel("Department:");
        final JTextField deptField = new JTextField();

        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back");

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.add(saveButton);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.add(backButton);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int studentId = Usersession.getUserId();

        if (studentId == 0) {
            JOptionPane.showMessageDialog(editFrame, "Error: You are not logged in.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                conn = DatabaseManager.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(editFrame, "Could not connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String sql = "SELECT * FROM users WHERE user_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, studentId);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        nameField.setText(rs.getString("username"));
                        emailField.setText(rs.getString("email"));
                        regNoField.setText(rs.getString("reg_no"));
                        phoneField.setText(rs.getString("phone"));
                        deptField.setText(rs.getString("department"));

                        String gender = rs.getString("gender");
                        if ("Male".equals(gender)) {
                            maleButton.setSelected(true);
                        } else if ("Female".equals(gender)) {
                            femaleButton.setSelected(true);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(editFrame, "Error loading profile data.", "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText();
                String newEmail = emailField.getText();
                String newRegNo = regNoField.getText();
                String newPhone = phoneField.getText();
                String newDept = deptField.getText();
                String newGender = null;
                if (maleButton.isSelected()) {
                    newGender = "Male";
                } else if (femaleButton.isSelected()) {
                    newGender = "Female";
                }

                if (newName.isEmpty() || newEmail.isEmpty() || newRegNo.isEmpty() || newPhone.isEmpty() || newDept.isEmpty() || newGender == null) {
                    JOptionPane.showMessageDialog(editFrame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                try {
                    conn = DatabaseManager.getConnection();
                    if (conn == null) {
                        JOptionPane.showMessageDialog(editFrame, "Could not connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String sql = "UPDATE users SET username = ?, email = ?, reg_no = ?, phone = ?, department = ?, gender = ? " +
                            "WHERE user_id = ?";

                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, newName);
                    stmt.setString(2, newEmail);
                    stmt.setString(3, newRegNo);
                    stmt.setString(4, newPhone);
                    stmt.setString(5, newDept);
                    stmt.setString(6, newGender);
                    stmt.setInt(7, Usersession.getUserId());

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(editFrame, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ViewProfilepage.createAndShowGUI();
                        editFrame.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(editFrame, "Error saving profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    try {
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewProfilepage.createAndShowGUI();
                editFrame.dispose();
            }
        });

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);
        mainPanel.add(genderLabel);
        mainPanel.add(genderPanel);
        mainPanel.add(regNoLabel);
        mainPanel.add(regNoField);
        mainPanel.add(phoneLabel);
        mainPanel.add(phoneField);
        mainPanel.add(deptLabel);
        mainPanel.add(deptField);
        mainPanel.add(backPanel);
        mainPanel.add(savePanel);

        editFrame.add(mainPanel);
        editFrame.setLocationRelativeTo(null);
        editFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Usersession.login(1, "testuser");
        createAndShowGUI();
    }
}