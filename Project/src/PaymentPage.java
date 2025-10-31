import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentPage {

    public static void createAndShowGUI(final int eventId, final String eventName, final BigDecimal fee) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        final JFrame paymentFrame = new JFrame("Payment");
        paymentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        paymentFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) paymentFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("Complete Payment");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel eventLabel = new JLabel("Event: " + eventName);
        eventLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        eventLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String feeString = (fee == null || fee.compareTo(BigDecimal.ZERO) == 0) ? "Free" : currencyFormatter.format(fee);
        JLabel feeLabel = new JLabel("Amount Due: " + feeString);
        feeLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        feeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feeLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel methodLabel = new JLabel("Select Payment Method:");
        methodLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        methodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        methodLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JRadioButton upiButton = new JRadioButton("UPI");
        upiButton.setActionCommand("UPI");
        upiButton.setSelected(true);
        upiButton.setOpaque(false);
        upiButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        upiButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton cardButton = new JRadioButton("Credit/Debit Card");
        cardButton.setActionCommand("Card");
        cardButton.setOpaque(false);
        cardButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton netBankingButton = new JRadioButton("Net Banking");
        netBankingButton.setActionCommand("Net Banking");
        netBankingButton.setOpaque(false);
        netBankingButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        netBankingButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        final ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(upiButton);
        paymentGroup.add(cardButton);
        paymentGroup.add(netBankingButton);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioPanel.add(upiButton);
        radioPanel.add(cardButton);
        radioPanel.add(netBankingButton);

        detailsPanel.add(eventLabel);
        detailsPanel.add(feeLabel);
        detailsPanel.add(methodLabel);
        detailsPanel.add(radioPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JButton payButton = new JButton("Pay Now");
        JButton cancelButton = new JButton("Cancel");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Insets buttonPadding = new Insets(8, 20, 8, 20);

        payButton.setFont(buttonFont);
        payButton.setBackground(new Color(60, 180, 80));
        payButton.setForeground(Color.WHITE);
        payButton.setMargin(buttonPadding);

        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setMargin(buttonPadding);

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int studentId = Usersession.getUserId();
                ButtonModel selectedButton = paymentGroup.getSelection();
                String paymentMethod = (selectedButton != null) ? selectedButton.getActionCommand() : null;
                String paymentStatus = "Paid";

                if (studentId == 0) {
                    JOptionPane.showMessageDialog(paymentFrame, "Error: You are not logged in.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (paymentMethod == null) {
                    JOptionPane.showMessageDialog(paymentFrame, "Please select a payment method.", "Payment Method Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                try {
                    conn = DatabaseManager.getConnection();
                    if (conn == null) throw new SQLException("Cannot connect to database.");

                    String sql = "INSERT INTO registrations (student_id, event_id, status, payment_method, payment_status) " +
                            "VALUES (?, ?, 'Registered', ?, ?)";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, eventId);
                    stmt.setString(3, paymentMethod);
                    stmt.setString(4, paymentStatus);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(paymentFrame, "Registration and Payment Successful for Event ID: " + eventId, "Success", JOptionPane.INFORMATION_MESSAGE);
                        StudentDashboard.createAndShowGUI();
                        paymentFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(paymentFrame, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        JOptionPane.showMessageDialog(paymentFrame, "You are already registered for this event.", "Already Registered", JOptionPane.WARNING_MESSAGE);
                        StudentDashboard.createAndShowGUI();
                        paymentFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(paymentFrame, "A database error occurred during registration.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } finally {
                    try { if (stmt != null) stmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewEventsPage.createAndShowGUI();
                paymentFrame.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(payButton);

        paymentFrame.add(titleLabel, BorderLayout.NORTH);
        paymentFrame.add(detailsPanel, BorderLayout.CENTER);
        paymentFrame.add(buttonPanel, BorderLayout.SOUTH);

        paymentFrame.pack();
        paymentFrame.setLocationRelativeTo(null);
        paymentFrame.setVisible(true);
    }

    public static void main(String[] args) {
        final int testEventId = 1;
        final String testEventName = "Sample Event";
        final BigDecimal testFee = new BigDecimal("50.00");
        Usersession.login(1, "testuser");
        createAndShowGUI(testEventId, testEventName, testFee);
    }
}