import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantPageList {

    public static void createAndShowGUI(int eventId) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame viewFrame = new JFrame("Event Participant List");
        viewFrame.setSize(600, 500);
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Participant List for Event ID: " + eventId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        final JTextArea participantTextArea = new JTextArea();
        participantTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        participantTextArea.setEditable(false);
        participantTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(participantTextArea);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                participantTextArea.setText("Error: Could not connect to database.");
            } else {
                String sql = "SELECT u.username, u.email, u.reg_no, u.department, u.phone " +
                        "FROM users u " +
                        "JOIN registrations r ON u.user_id = r.student_id " +
                        "WHERE r.event_id = ? AND u.role = 'student'";

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, eventId);
                rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    participantTextArea.setText("No students are registered for this event yet.");
                } else {
                    while (rs.next()) {
                        String name = "Name: " + rs.getString("username");
                        String regNo = "Reg No: " + rs.getString("reg_no");
                        String dept = "Dept: " + rs.getString("department");
                        String email = "Email: " + rs.getString("email");
                        String phone = "Phone: " + rs.getString("phone");

                        participantTextArea.append(name + " (" + regNo + ")\n");
                        participantTextArea.append(dept + "\n");
                        participantTextArea.append(email + " | " + phone + "\n");
                        participantTextArea.append("--------------------------------------------------\n\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            participantTextArea.setText("Error loading participant data.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton backButton = new JButton("Back to My Events");
        bottomPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewEventAdmin.createAndShowGUI();
                viewFrame.dispose();
            }
        });

        viewFrame.add(titleLabel, BorderLayout.NORTH);
        viewFrame.add(scrollPane, BorderLayout.CENTER);
        viewFrame.add(bottomPanel, BorderLayout.SOUTH);

        viewFrame.setLocationRelativeTo(null);
        viewFrame.setVisible(true);
    }

    public static void main(String[] args) {
        int testEventId = 1;
        createAndShowGUI(testEventId);
    }
}