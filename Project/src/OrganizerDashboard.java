import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrganizerDashboard {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame dashboardFrame = new JFrame("Organizer Dashboard");
        dashboardFrame.setSize(400, 350);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = (JPanel) dashboardFrame.getContentPane();
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Organizer Menu");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel actionButtonsPanel = new JPanel();
        actionButtonsPanel.setLayout(new BoxLayout(actionButtonsPanel, BoxLayout.Y_AXIS));
        actionButtonsPanel.setOpaque(false);

        JButton createEventButton = new JButton("Create New Event");
        JButton viewEventsButton = new JButton("View My Events");
        JButton viewTransactionsButton = new JButton("View Transactions");

        Font mainButtonFont = new Font("Tahoma", Font.PLAIN, 16);
        Dimension buttonSize = new Dimension(200, 40);

        createEventButton.setFont(mainButtonFont);
        createEventButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createEventButton.setMaximumSize(buttonSize);

        viewEventsButton.setFont(mainButtonFont);
        viewEventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewEventsButton.setMaximumSize(buttonSize);

        viewTransactionsButton.setFont(mainButtonFont);
        viewTransactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewTransactionsButton.setMaximumSize(buttonSize);

        actionButtonsPanel.add(createEventButton);
        actionButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        actionButtonsPanel.add(viewEventsButton);
        actionButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        actionButtonsPanel.add(viewTransactionsButton);
        actionButtonsPanel.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        logoutPanel.setOpaque(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        logoutPanel.add(logoutButton);

        createEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateEventForm.createAndShowGUI();
                dashboardFrame.dispose();
            }
        });

        viewEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewEventAdmin.createAndShowGUI();
                dashboardFrame.dispose();
            }
        });

        viewTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransactionViewPage.createAndShowGUI();
                dashboardFrame.dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usersession.logout();
                RoleSelection.main(null);
                dashboardFrame.dispose();
            }
        });

        dashboardFrame.add(titleLabel, BorderLayout.NORTH);
        dashboardFrame.add(actionButtonsPanel, BorderLayout.CENTER);
        dashboardFrame.add(logoutPanel, BorderLayout.SOUTH);

        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}