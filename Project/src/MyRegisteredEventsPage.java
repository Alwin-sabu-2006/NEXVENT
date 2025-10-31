import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyRegisteredEventsPage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame viewFrame = new JFrame("My Registered Events");
        viewFrame.setSize(600, 500);
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Registered Events");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        final JTextArea eventTextArea = new JTextArea();
        eventTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        eventTextArea.setEditable(false);
        eventTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(eventTextArea);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int studentId = Usersession.getUserId();
        if (studentId == 0) {
            eventTextArea.setText("Error: You are not logged in.");
        } else {
            try {
                conn = DatabaseManager.getConnection();
                if (conn == null) {
                    eventTextArea.setText("Error: Could not connect to database.");
                } else {
                    String sql = "SELECT e.event_id, e.event_name, e.event_date, e.location, e.description " +
                            "FROM events e " +
                            "JOIN registrations r ON e.event_id = r.event_id " +
                            "WHERE r.student_id = ?";

                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, studentId);
                    rs = stmt.executeQuery();

                    if (!rs.isBeforeFirst()) {
                        eventTextArea.setText("You are not registered for any events.");
                    } else {
                        while (rs.next()) {
                            String id = "ID: " + rs.getInt("event_id");
                            String name = "Event: " + rs.getString("event_name");
                            String date = "Date: " + rs.getString("event_date");
                            String loc = "Location: " + rs.getString("location");
                            String desc = "Desc: " + rs.getString("description");

                            eventTextArea.append(id + "\n");
                            eventTextArea.append(name + " (" + date + ")\n");
                            eventTextArea.append(loc + "\n");
                            eventTextArea.append(desc + "\n");
                            eventTextArea.append("--------------------------------------------------\n\n");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                eventTextArea.setText("Error loading events from database.");
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

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel messageLabel = new JLabel("Registered Events Cannot Be Cancelled.");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setForeground(Color.GRAY);

        JButton backButton = new JButton("Back to Dashboard");

        bottomPanel.add(messageLabel);
        bottomPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDashboard.createAndShowGUI();
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
        Usersession.login(1, "testuser");
        createAndShowGUI();
    }
}