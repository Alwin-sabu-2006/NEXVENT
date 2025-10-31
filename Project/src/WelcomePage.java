import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage {

    public static void createAndShowGUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        final JFrame welcomeFrame = new JFrame("Welcome");
        welcomeFrame.setSize(550, 400);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLayout(new BorderLayout(15, 15));

        JPanel contentPanel = (JPanel) welcomeFrame.getContentPane();
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("NEXVENT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel secondLabel = new JLabel("The Next Generation Of Event Management");
        secondLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        secondLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        secondLabel.setForeground(Color.DARK_GRAY);
        secondLabel.setBorder(new EmptyBorder(0, 10, 20, 10));

        topPanel.add(titleLabel);
        topPanel.add(secondLabel);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setText("An Event Participation Manager that helps organizers manage events  \t \tand \n \tAllows users to register for them easily.");
        descriptionArea.setFont(new Font("Californian FB", Font.PLAIN, 16));
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton continueButton = new JButton("Get Started");
        continueButton.setFont(new Font("Arial", Font.BOLD, 18));
        continueButton.setMargin(new Insets(10, 25, 10, 25));
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(continueButton);

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Get Started button clicked - Opening Role Selection");
                RoleSelection.main(null);
                welcomeFrame.dispose();
            }
        });

        welcomeFrame.add(topPanel, BorderLayout.NORTH);
        welcomeFrame.add(descriptionArea, BorderLayout.CENTER);
        welcomeFrame.add(buttonPanel, BorderLayout.SOUTH);

        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}