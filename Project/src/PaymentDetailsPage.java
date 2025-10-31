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
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentDetailsPage {

    public static void createAndShowGUI(final int eventId) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        final JFrame detailsFrame = new JFrame("Payment Details for Event ID: " + eventId);
        detailsFrame.setSize(800, 600);
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) detailsFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Payment Details for Event ID: " + eventId);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 15, 15, 15));

        final JTextArea detailsTextArea = new JTextArea(20, 70);
        detailsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsTextArea.setEditable(false);
        detailsTextArea.setBackground(new Color(250, 253, 255));
        detailsTextArea.setForeground(Color.DARK_GRAY);
        detailsTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(detailsTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        final JTextArea summaryTextArea = new JTextArea(4, 70);
        summaryTextArea.setFont(new Font("Monospaced", Font.BOLD, 13));
        summaryTextArea.setEditable(false);
        summaryTextArea.setBackground(new Color(230, 240, 255));
        summaryTextArea.setForeground(new Color(50, 50, 50));
        summaryTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, BigDecimal> totalsByMethod = new HashMap<>();

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                detailsTextArea.setText("Error: Could not connect to database.");
                summaryTextArea.setText("Could not calculate totals.");
            } else {
                String sql = "SELECT u.username, u.reg_no, e.registration_fee, " +
                        "r.payment_method, r.payment_status, r.registration_timestamp " +
                        "FROM registrations r " +
                        "JOIN users u ON r.student_id = u.user_id " +
                        "JOIN events e ON r.event_id = e.event_id " +
                        "WHERE r.event_id = ? AND r.payment_status = 'Paid' " +
                        "ORDER BY r.registration_timestamp ASC";

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, eventId);
                rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    detailsTextArea.setText("No paid registrations found for this event.");
                    summaryTextArea.setText("No totals to display.");
                } else {
                    StringBuilder sbDetails = new StringBuilder();
                    sbDetails.append(String.format("%-20s | %-15s | %-12s | %-10s | %-19s\n",
                            "Student Name", "Reg No", "Method", "Fee", "Timestamp"));
                    sbDetails.append("--------------------------------------------------------------------------------------\n");

                    while (rs.next()) {
                        String studentName = rs.getString("username");
                        String regNo = rs.getString("reg_no");
                        BigDecimal fee = rs.getBigDecimal("registration_fee");
                        String method = rs.getString("payment_method");
                        String status = rs.getString("payment_status");
                        Timestamp timestamp = rs.getTimestamp("registration_timestamp");

                        String feeStr = (fee == null || fee.compareTo(BigDecimal.ZERO) == 0) ? "Free" : currencyFormatter.format(fee);
                        String timeStr = (timestamp == null) ? "N/A" : dateFormat.format(timestamp);

                        sbDetails.append(String.format("%-20.20s | %-15.15s | %-12s | %-10s | %-19s\n",
                                studentName, regNo, method, feeStr, timeStr));

                        if (fee != null) {
                            totalAmount = totalAmount.add(fee);
                            totalsByMethod.put(method, totalsByMethod.getOrDefault(method, BigDecimal.ZERO).add(fee));
                        }
                    }
                    detailsTextArea.setText(sbDetails.toString());
                    detailsTextArea.setCaretPosition(0);

                    StringBuilder sbSummary = new StringBuilder("Payment Summary:\n");
                    for (Map.Entry<String, BigDecimal> entry : totalsByMethod.entrySet()) {
                        sbSummary.append(String.format("  - %-12s: %s\n", entry.getKey(), currencyFormatter.format(entry.getValue())));
                    }
                    sbSummary.append("-----------------------------\n");
                    sbSummary.append(String.format("  Total Received: %s", currencyFormatter.format(totalAmount)));
                    summaryTextArea.setText(sbSummary.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            detailsTextArea.setText("Error loading payment details.");
            summaryTextArea.setText("Error calculating totals.");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(summaryTextArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(15, 10, 0, 10));
        bottomPanel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Insets buttonPadding = new Insets(5, 15, 5, 15);

        JButton backButton = new JButton("Back to Transaction Input");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setMargin(buttonPadding);
        bottomPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransactionViewPage.createAndShowGUI();
                detailsFrame.dispose();
            }
        });

        detailsFrame.add(titleLabel, BorderLayout.NORTH);
        detailsFrame.add(centerPanel, BorderLayout.CENTER);
        detailsFrame.add(bottomPanel, BorderLayout.SOUTH);

        detailsFrame.setLocationRelativeTo(null);
        detailsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        final int testEventId = 1;
        createAndShowGUI(testEventId);
    }
}