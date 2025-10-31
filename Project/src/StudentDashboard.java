import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentDashboard {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame dashboardFrame = new JFrame("Student Dashboard");
        dashboardFrame.setSize(400, 350);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = (JPanel) dashboardFrame.getContentPane();
        contentPanel.setBackground(new Color(240, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));


        String username = Usersession.getUsername();
        String greeting = "Hello, " + (username != null ? username : "Student") + "!"; // Create greeting

        JLabel titleLabel = new JLabel(greeting);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel actionButtonsPanel = new JPanel();
        actionButtonsPanel.setLayout(new BoxLayout(actionButtonsPanel, BoxLayout.Y_AXIS));
        actionButtonsPanel.setOpaque(false);

        JButton viewAllEventsButton = new JButton("View All Events");
        JButton myEventsButton = new JButton("My Registered Events");
        JButton viewProfileButton = new JButton("My Profile"); // Changed text slightly

        Font mainButtonFont = new Font("Tahoma", Font.PLAIN, 16);
        Dimension buttonSize = new Dimension(200, 40);

        viewAllEventsButton.setFont(mainButtonFont);
        viewAllEventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewAllEventsButton.setMaximumSize(buttonSize);

        myEventsButton.setFont(mainButtonFont);
        myEventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myEventsButton.setMaximumSize(buttonSize);

        viewProfileButton.setFont(mainButtonFont);
        viewProfileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewProfileButton.setMaximumSize(buttonSize);

        actionButtonsPanel.add(viewAllEventsButton);
        actionButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        actionButtonsPanel.add(myEventsButton);
        actionButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        actionButtonsPanel.add(viewProfileButton);
        actionButtonsPanel.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        logoutPanel.setOpaque(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        logoutPanel.add(logoutButton);

        viewAllEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ViewEventsPage.createAndShowGUI();
                dashboardFrame.dispose();
            }
        });

        myEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyRegisteredEventsPage.createAndShowGUI();
                dashboardFrame.dispose();
            }
        });

        viewProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewProfilepage.createAndShowGUI();
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

        Usersession.login(1, "Alwin");
        createAndShowGUI();
    }
}