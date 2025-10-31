import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Year;
import java.math.BigDecimal;

public class CreateEventForm {

    public static void createAndShowGUI() {

        try {
            // Keep Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame eventFrame = new JFrame("Create New Event");
        // Adjusted size for the extra field
        eventFrame.setSize(500, 450);
        eventFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a standard layout with default gaps and padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(7, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Labels and Fields (using default fonts)
        JLabel nameLabel = new JLabel("Event Name:");
        final JTextField nameField = new JTextField();

        JLabel dateLabel = new JLabel("Event Date:");
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.format("%02d", i + 1);
        }
        final JComboBox<String> dayBox = new JComboBox<>(days);

        String[] months = {"01-Jan", "02-Feb", "03-Mar", "04-Apr", "05-May", "06-Jun",
                "07-Jul", "08-Aug", "09-Sep", "10-Oct", "11-Nov", "12-Dec"};
        final JComboBox<String> monthBox = new JComboBox<>(months);

        int currentYear = Year.now().getValue();
        String[] years = new String[6];
        for (int i = 0; i < 6; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        final JComboBox<String> yearBox = new JComboBox<>(years);

        datePanel.add(dayBox);
        datePanel.add(monthBox);
        datePanel.add(yearBox);

        JLabel locationLabel = new JLabel("Event Location:");
        final JTextField locationField = new JTextField();

        JLabel seatsLabel = new JLabel("Number of Seats:");
        final JTextField seatsField = new JTextField();

        JLabel feeLabel = new JLabel("Registration Fee:");
        final JTextField feeField = new JTextField();

        JLabel descLabel = new JLabel("Description:");
        final JTextArea descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        // Use default border provided by JScrollPane
        JScrollPane descScrollPane = new JScrollPane(descArea);

        JButton createButton = new JButton("Create Event");
        JButton backButton = new JButton("Back");

        // Use simple panels for buttons to keep them small
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        createPanel.add(createButton);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.add(backButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventName = nameField.getText();
                String location = locationField.getText();
                String description = descArea.getText();
                String seatsText = seatsField.getText();
                String feeText = feeField.getText();
                int organizerId = Usersession.getUserId();

                String selectedDay = (String) dayBox.getSelectedItem();
                String selectedMonthStr = (String) monthBox.getSelectedItem();
                String selectedYear = (String) yearBox.getSelectedItem();
                String selectedMonth = selectedMonthStr.substring(0, 2);
                String eventDate = selectedYear + "-" + selectedMonth + "-" + selectedDay;

                int maxSeats;
                BigDecimal registrationFee;

                if (eventName.isEmpty() || location.isEmpty() || seatsText.isEmpty() || feeText.isEmpty()) {
                    JOptionPane.showMessageDialog(eventFrame, "Please fill in all required fields (Name, Location, Seats, Fee).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    maxSeats = Integer.parseInt(seatsText);
                    if (maxSeats <= 0) throw new NumberFormatException("Seats must be positive.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(eventFrame, "Please enter a valid positive whole number for Seats.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    registrationFee = new BigDecimal(feeText);
                    if (registrationFee.compareTo(BigDecimal.ZERO) < 0) {
                        throw new NumberFormatException("Fee cannot be negative.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(eventFrame, "Please enter a valid non-negative number for Registration Fee (e.g., 50.00 or 0).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                if (organizerId == 0) {
                    JOptionPane.showMessageDialog(eventFrame, "Error: You are not logged in.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                try {
                    conn = DatabaseManager.getConnection();
                    if (conn == null) throw new SQLException("Cannot connect to database.");

                    String sql = "INSERT INTO events (event_name, event_date, location, description, organizer_id, max_seats, registration_fee) VALUES (?, ?, ?, ?, ?, ?, ?)";

                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, eventName);
                    stmt.setString(2, eventDate);
                    stmt.setString(3, location);
                    stmt.setString(4, description);
                    stmt.setInt(5, organizerId);
                    stmt.setInt(6, maxSeats);
                    stmt.setBigDecimal(7, registrationFee);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(eventFrame, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        OrganizerDashboard.createAndShowGUI();
                        eventFrame.dispose();
                    }

                } catch (SQLException ex) {
                    if (ex.getMessage().contains("Incorrect date value")) {
                        JOptionPane.showMessageDialog(eventFrame, "Database Error: Incorrect date value.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(eventFrame, "A database error occurred.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
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
                OrganizerDashboard.createAndShowGUI();
                eventFrame.dispose();
            }
        });

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(dateLabel);
        mainPanel.add(datePanel);
        mainPanel.add(locationLabel);
        mainPanel.add(locationField);
        mainPanel.add(seatsLabel);
        mainPanel.add(seatsField);
        mainPanel.add(feeLabel);
        mainPanel.add(feeField);
        mainPanel.add(descLabel);
        mainPanel.add(descScrollPane);
        mainPanel.add(backPanel);
        mainPanel.add(createPanel);

        eventFrame.add(mainPanel);
        eventFrame.setLocationRelativeTo(null);
        eventFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}