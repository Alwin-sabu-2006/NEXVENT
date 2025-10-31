import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentAuthPage {

    public static void createAndShowGUI() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        final JFrame authFrame = new JFrame("Student Portal");
        authFrame.setSize(450, 250);
        authFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        authFrame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome, Student");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(30, 20, 15, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 15));
        buttonPanel.setBorder(new EmptyBorder(15, 30, 40, 30));

        JButton createButton = new JButton("Create New Account");
        JButton loginButton = new JButton("Already Have an Account");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        createButton.setFont(buttonFont);
        loginButton.setFont(buttonFont);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentSignupPage.createAndShowGUI();
                authFrame.dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentLoginPage.createAndShowGUI();
                authFrame.dispose();
            }
        });

        buttonPanel.add(createButton);
        buttonPanel.add(loginButton);

        authFrame.add(titleLabel, BorderLayout.NORTH);
        authFrame.add(buttonPanel, BorderLayout.CENTER);

        authFrame.setLocationRelativeTo(null);
        authFrame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}