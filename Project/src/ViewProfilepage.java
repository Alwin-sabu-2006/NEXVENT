import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewProfilepage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame profileFrame = new JFrame("Student Profile");
        profileFrame.setSize(450, 400);
        profileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        profileFrame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(6, 2, 10, 10));
        profilePanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        Font fieldFont = new Font("Arial", Font.BOLD, 14);
        Font dataFont = new Font("Arial", Font.PLAIN, 14);

        profilePanel.add(new JLabel("Name:") {{ setFont(fieldFont); }});
        final JLabel nameDataLabel = new JLabel();
        nameDataLabel.setFont(dataFont);
        profilePanel.add(nameDataLabel);

        profilePanel.add(new JLabel("Email:") {{ setFont(fieldFont); }});
        final JLabel emailDataLabel = new JLabel();
        emailDataLabel.setFont(dataFont);
        profilePanel.add(emailDataLabel);

        profilePanel.add(new JLabel("Gender:") {{ setFont(fieldFont); }});
        final JLabel genderDataLabel = new JLabel();
        genderDataLabel.setFont(dataFont);
        profilePanel.add(genderDataLabel);

        profilePanel.add(new JLabel("Registration Number:") {{ setFont(fieldFont); }});
        final JLabel regNoDataLabel = new JLabel();
        regNoDataLabel.setFont(dataFont);
        profilePanel.add(regNoDataLabel);

        profilePanel.add(new JLabel("Phone Number:") {{ setFont(fieldFont); }});
        final JLabel phoneDataLabel = new JLabel();
        phoneDataLabel.setFont(dataFont);
        profilePanel.add(phoneDataLabel);

        profilePanel.add(new JLabel("Department:") {{ setFont(fieldFont); }});
        final JLabel deptDataLabel = new JLabel();
        deptDataLabel.setFont(dataFont);
        profilePanel.add(deptDataLabel);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int studentId = Usersession.getUserId();

        if (studentId == 0) {
            JOptionPane.showMessageDialog(profileFrame, "Error: You are not logged in.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                conn = DatabaseManager.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(profileFrame, "Could not connect to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String sql = "SELECT * FROM users WHERE user_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, studentId);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        nameDataLabel.setText(rs.getString("username"));
                        emailDataLabel.setText(rs.getString("email"));
                        genderDataLabel.setText(rs.getString("gender"));
                        regNoDataLabel.setText(rs.getString("reg_no"));
                        phoneDataLabel.setText(rs.getString("phone"));
                        deptDataLabel.setText(rs.getString("department"));
                    } else {
                        JOptionPane.showMessageDialog(profileFrame, "Could not find user data.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(profileFrame, "Error loading profile data.", "Database Error", JOptionPane.ERROR_MESSAGE);
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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton editButton = new JButton("Edit Profile");
        JButton backButton = new JButton("Back to Dashboard");

        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditProfilePage.createAndShowGUI();
                profileFrame.dispose();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDashboard.createAndShowGUI();
                profileFrame.dispose();
            }
        });

        profileFrame.add(titleLabel, BorderLayout.NORTH);
        profileFrame.add(profilePanel, BorderLayout.CENTER);
        profileFrame.add(buttonPanel, BorderLayout.SOUTH);

        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Usersession.login(1, "testuser");
        createAndShowGUI();
    }
}