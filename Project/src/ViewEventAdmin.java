import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ViewEventAdmin {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final boolean isAdminView = true;
        String frameTitle = "View My Created Events";
        String pageTitle = "My Created Events";

        final JFrame viewFrame = new JFrame(frameTitle);
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) viewFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(pageTitle);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 15, 15, 15));

        final JTextArea eventTextArea = new JTextArea(15, 50);
        eventTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        eventTextArea.setEditable(false);
        eventTextArea.setBackground(new Color(250, 253, 255));
        eventTextArea.setForeground(Color.DARK_GRAY);
        eventTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(eventTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PreparedStatement countStmt = null;
        ResultSet countRs = null;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        int currentUserId = Usersession.getUserId();

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                eventTextArea.setText("Error: Could not connect to database.");
            } else {
                String sql = "SELECT event_id, event_name, event_date, location, description, max_seats, registration_fee FROM events WHERE organizer_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentUserId);

                rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    eventTextArea.setText("You have not created any events yet.");
                } else {
                    String countSql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
                    countStmt = conn.prepareStatement(countSql);

                    while (rs.next()) {
                        int eventId = rs.getInt("event_id");
                        String name = "Event: " + rs.getString("event_name");
                        String date = "Date: " + rs.getString("event_date");
                        String loc = "Location: " + rs.getString("location");
                        String desc = "Desc: " + rs.getString("description");
                        int maxSeats = rs.getInt("max_seats");
                        BigDecimal fee = rs.getBigDecimal("registration_fee");

                        countStmt.setInt(1, eventId);
                        countRs = countStmt.executeQuery();
                        int currentRegistrations = 0;
                        if (countRs.next()) {
                            currentRegistrations = countRs.getInt(1);
                        }
                        countRs.close();

                        String seatStatus = "";
                        if (maxSeats > 0) {
                            if (currentRegistrations >= maxSeats) {
                                seatStatus = " (Seats Full)";
                            } else {
                                seatStatus = " (" + currentRegistrations + "/" + maxSeats + " seats)";
                            }
                        } else {
                            seatStatus = " (Seats Available)";
                        }

                        String feeString = (fee == null || fee.compareTo(BigDecimal.ZERO) == 0) ? "Free" : currencyFormatter.format(fee);

                        eventTextArea.append("ID: " + eventId + "\n");
                        eventTextArea.append(name + " (" + date + ")" + seatStatus + "\n");
                        eventTextArea.append(loc + " | Fee: " + feeString + "\n");
                        eventTextArea.append(desc + "\n");
                        eventTextArea.append("--------------------------------------------------\n\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            eventTextArea.setText("Error loading events from database.");
            if (currentUserId == 0 && isAdminView) {
                eventTextArea.setText("Error: Admin user ID not found. Please log in again.");
            }
        } finally {
            try { if (countRs != null) countRs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (countStmt != null) countStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JLabel idLabel = new JLabel("Enter Event ID:");
        idLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        final JTextField idField = new JTextField(10);
        idField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        JButton viewParticipantsButton = new JButton("View Participants");
        viewParticipantsButton.setFont(buttonFont);
        viewParticipantsButton.setBackground(new Color(100, 150, 220));
        viewParticipantsButton.setForeground(Color.WHITE);
        viewParticipantsButton.setMargin(new Insets(5, 10, 5, 10));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setMargin(new Insets(5, 10, 5, 10));

        bottomPanel.add(idLabel);
        bottomPanel.add(idField);
        bottomPanel.add(viewParticipantsButton);
        bottomPanel.add(backButton);

        viewParticipantsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventIdText = idField.getText();
                if (eventIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(viewFrame, "Please enter an Event ID.", "No ID Entered", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    int eventId = Integer.parseInt(eventIdText);
                    ParticipantPageList.createAndShowGUI(eventId);
                    viewFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(viewFrame, "Please enter a valid number for the Event ID.", "Invalid ID", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrganizerDashboard.createAndShowGUI();
                viewFrame.dispose();
            }
        });

        viewFrame.add(titleLabel, BorderLayout.NORTH);
        viewFrame.add(scrollPane, BorderLayout.CENTER);
        viewFrame.add(bottomPanel, BorderLayout.SOUTH);

        viewFrame.pack();
        viewFrame.setLocationRelativeTo(null);
        viewFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Usersession.login(1, "testadmin");
        createAndShowGUI();
    }
}
