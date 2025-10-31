import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionViewPage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame inputFrame = new JFrame("View Event Transactions");
        inputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inputFrame.setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = (JPanel) inputFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("View Payment Details for Event");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel idInputLabel = new JLabel("Enter Event ID:");
        idInputLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        final JTextField idInputField = new JTextField(10);
        idInputField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        inputPanel.add(idInputLabel);
        inputPanel.add(idInputField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Insets buttonPadding = new Insets(5, 15, 5, 15);

        JButton viewButton = new JButton("View Details");
        viewButton.setFont(buttonFont);
        viewButton.setBackground(new Color(100, 150, 220));
        viewButton.setForeground(Color.WHITE);
        viewButton.setMargin(buttonPadding);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setMargin(buttonPadding);

        buttonPanel.add(backButton);
        buttonPanel.add(viewButton);

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventIdText = idInputField.getText();
                if (eventIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(inputFrame, "Please enter an Event ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    int eventId = Integer.parseInt(eventIdText);
                    PaymentDetailsPage.createAndShowGUI(eventId);
                    inputFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(inputFrame, "Please enter a valid number for the Event ID.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrganizerDashboard.createAndShowGUI();
                inputFrame.dispose();
            }
        });

        inputFrame.add(titleLabel, BorderLayout.NORTH);
        inputFrame.add(inputPanel, BorderLayout.CENTER);
        inputFrame.add(buttonPanel, BorderLayout.SOUTH);

        inputFrame.pack();
        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}