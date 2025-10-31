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

public class EventConfirmationPage {

    public static void createAndShowGUI(final int eventId) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        final JFrame confirmFrame = new JFrame("Confirm Event Registration");
        confirmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) confirmFrame.getContentPane();
        contentPanel.setBackground(new Color(245, 248, 250));
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Confirm Registration Details");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 15, 15, 15));

        final JTextArea detailsArea = new JTextArea();
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(250, 253, 255));
        detailsArea.setForeground(Color.DARK_GRAY);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BigDecimal eventFee = BigDecimal.ZERO;
        String eventName = "N/A";

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                detailsArea.setText("Error: Could not connect to database.");
            } else {
                String sql = "SELECT event_name, event_date, location, description, registration_fee FROM events WHERE event_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, eventId);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    eventName = rs.getString("event_name");
                    String date = rs.getString("event_date");
                    String loc = rs.getString("location");
                    String desc = rs.getString("description");
                    eventFee = rs.getBigDecimal("registration_fee");
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    String feeString = (eventFee == null || eventFee.compareTo(BigDecimal.ZERO) == 0) ? "Free" : currencyFormatter.format(eventFee);

                    detailsArea.setText("You are about to register for:\n\n");
                    detailsArea.append("Event Name: " + eventName + "\n");
                    detailsArea.append("Date:       " + date + "\n");
                    detailsArea.append("Location:   " + loc + "\n");
                    detailsArea.append("Fee:        " + feeString + "\n\n");
                    detailsArea.append("Description:\n" + desc + "\n\n");
                    detailsArea.append("Warning: Registration cannot be cancelled once payment is made.");

                } else {
                    detailsArea.setText("Error: Event details not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            detailsArea.setText("Error loading event details.");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton confirmButton = new JButton("Confirm & Pay");
        JButton cancelButton = new JButton("Cancel");
        JButton backToDashButton = new JButton("Back to Dashboard");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Insets buttonPadding = new Insets(8, 20, 8, 20);

        confirmButton.setFont(buttonFont);
        confirmButton.setBackground(new Color(60, 180, 80));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setMargin(buttonPadding);

        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(new Color(220, 100, 100));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setMargin(buttonPadding);

        backToDashButton.setFont(buttonFont);
        backToDashButton.setBackground(new Color(200, 200, 200));
        backToDashButton.setMargin(buttonPadding);

        final BigDecimal finalEventFee = eventFee;
        final String finalEventName = eventName;

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentPage.createAndShowGUI(eventId, finalEventName, finalEventFee);
                confirmFrame.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewEventsPage.createAndShowGUI();
                confirmFrame.dispose();
            }
        });

        backToDashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDashboard.createAndShowGUI();
                confirmFrame.dispose();
            }
        });

        bottomButtonPanel.add(backToDashButton);
        bottomButtonPanel.add(cancelButton);
        bottomButtonPanel.add(confirmButton);

        confirmFrame.add(titleLabel, BorderLayout.NORTH);
        confirmFrame.add(scrollPane, BorderLayout.CENTER);
        confirmFrame.add(bottomButtonPanel, BorderLayout.SOUTH);

        confirmFrame.pack();
        confirmFrame.setLocationRelativeTo(null);
        confirmFrame.setVisible(true);
    }

    public static void main(String[] args) {
        final int testEventId = 1;
        Usersession.login(1, "testuser");
        createAndShowGUI(testEventId);
    }
}