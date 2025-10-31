import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoleSelection {

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        final JFrame mainFrame = new JFrame("Event Participation Manager");
        mainFrame.setSize(450, 200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        JLabel roleLabel = new JLabel("Select Your Role");
        roleLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        roleLabel.setForeground(new Color(50, 50, 100));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setBorder(new EmptyBorder(25, 10, 15, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 15));
        buttonPanel.setBorder(new EmptyBorder(10, 50, 40, 50));

        JButton adminButton = new JButton("Admin");
        JButton studentButton = new JButton("Student");


        Font buttonFont = new Font("Californian FB", Font.BOLD, 18);
        adminButton.setFont(buttonFont);
        studentButton.setFont(buttonFont);

        adminButton.setBackground(new Color(210, 225, 240));
        studentButton.setBackground(new Color(210, 225, 240));

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminAuthPage.createAndShowGUI();
                mainFrame.dispose();
            }
        });

        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentAuthPage.createAndShowGUI();
                mainFrame.dispose();
            }
        });


        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        adminPanel.add(adminButton);
        adminPanel.setOpaque(false);

        JPanel studentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        studentPanel.add(studentButton);
        studentPanel.setOpaque(false);

        buttonPanel.add(adminPanel);
        buttonPanel.add(studentPanel);


        mainFrame.add(roleLabel, BorderLayout.NORTH);
        mainFrame.add(buttonPanel, BorderLayout.CENTER);


        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}