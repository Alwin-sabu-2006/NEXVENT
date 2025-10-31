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

public class ViewEventsPage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame viewFrame = new JFrame("View All Events");
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) viewFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("All Available Events");
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

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                eventTextArea.setText("Error: Could not connect to database.");
            } else {
                String sql = "SELECT event_id, event_name, event_date, location, description, max_seats, registration_fee FROM events";
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    eventTextArea.setText("No events found.");
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
        } finally {
            try { if (countRs != null) countRs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (countStmt != null) countStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.setOpaque(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputPanel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JLabel idLabel = new JLabel("Enter Event ID to Register:");
        idLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        final JTextField idField = new JTextField(10);
        idField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        JButton registerButton = new JButton("Register");
        registerButton.setFont(buttonFont);
        registerButton.setBackground(new Color(60, 180, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setMargin(new Insets(5, 10, 5, 10));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setMargin(new Insets(5, 10, 5, 10));

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(registerButton);
        inputPanel.add(backButton);

        JLabel warningLabel = new JLabel("Warning: Registration once done cannot be cancelled.");
        warningLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 12));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(warningLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomPanel.add(inputPanel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventIdText = idField.getText();
                int studentId = Usersession.getUserId();
                if (studentId == 0) {
                    JOptionPane.showMessageDialog(viewFrame, "Error: You are not logged in.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int eventId;
                if (eventIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(viewFrame, "Please enter an Event ID.", "No ID Entered", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    eventId = Integer.parseInt(eventIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(viewFrame, "Please enter a valid number for the Event ID.", "Invalid ID", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection checkConn = null;
                PreparedStatement checkStmt = null;
                ResultSet checkRs = null;
                PreparedStatement countStmt = null;
                ResultSet countRs = null;
                boolean proceedToConfirm = false;


                try {
                    checkConn = DatabaseManager.getConnection();
                    if (checkConn == null) throw new SQLException("Cannot connect to database.");

                    String checkSql = "SELECT event_name, max_seats, registration_fee FROM events WHERE event_id = ?";
                    checkStmt = checkConn.prepareStatement(checkSql);
                    checkStmt.setInt(1, eventId);
                    checkRs = checkStmt.executeQuery();

                    if (!checkRs.next()) {
                        JOptionPane.showMessageDialog(viewFrame, "That Event ID does not exist.", "Invalid Event ID", JOptionPane.ERROR_MESSAGE);
                    } else {

                        int maxSeats = checkRs.getInt("max_seats");


                        if (maxSeats > 0) {
                            String countSql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
                            countStmt = checkConn.prepareStatement(countSql);
                            countStmt.setInt(1, eventId);
                            countRs = countStmt.executeQuery();
                            int currentRegistrations = 0;
                            if (countRs.next()) {
                                currentRegistrations = countRs.getInt(1);
                            }

                            if (currentRegistrations >= maxSeats) {
                                JOptionPane.showMessageDialog(viewFrame, "Sorry, this event is full (" + currentRegistrations + "/" + maxSeats + ").", "Seats Full", JOptionPane.WARNING_MESSAGE);
                            } else {
                                proceedToConfirm = true;
                            }
                        } else {
                            proceedToConfirm = true;
                        }
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(viewFrame, "Error checking event capacity.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    try { if (countRs != null) countRs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (countStmt != null) countStmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (checkRs != null) checkRs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (checkStmt != null) checkStmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (checkConn != null) checkConn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                }

                if (proceedToConfirm) {
                    EventConfirmationPage.createAndShowGUI(eventId);
                    viewFrame.dispose();
                }
            }
        });

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

        viewFrame.pack();
        viewFrame.setLocationRelativeTo(null);
        viewFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Usersession.login(1, "testuser");
        createAndShowGUI();
    }
}