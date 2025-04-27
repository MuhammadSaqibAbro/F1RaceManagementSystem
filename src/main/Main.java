package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        initComponents();
    }

    private void initComponents() {
        // Set the title with the golden color (RGB: 255, 215, 0)
        setTitle("F1 Race Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Panel to hold components with BorderLayout and padding
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load image from resources (assuming in the "main" package)
                ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/main/f1.jpg"));
                // Draw image at the size of the panel
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Creating a panel for header label with BoxLayout
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // Make the panel transparent

        // Adding an empty space to push the header label up
        headerPanel.add(Box.createVerticalStrut(100)); // Adjust the space as needed

        // Adding header label
        JLabel headerLabel = new JLabel("Welcome to F1 Race Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font and size
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align horizontally
        headerLabel.setForeground(new Color(255, 215, 0)); // Set golden color (RGB: 255, 215, 0)
        headerPanel.add(headerLabel); // Add to header panel

        // Adding button panel with "Go to Management" button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton goToManagementButton = new JButton("Go to Management");
        goToManagementButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        goToManagementButton.setForeground(Color.WHITE); // Set text color to white
        goToManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open MainApplication when button is clicked
                MainApplication mainApp = new MainApplication();
                mainApp.setVisible(true);
                dispose(); // Close current frame
            }
        });
        buttonPanel.add(goToManagementButton);
        buttonPanel.setOpaque(false); // Make the panel transparent

        // Adding headerPanel and buttonPanel to mainPanel in BorderLayout.CENTER
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main main = new Main();
                main.setVisible(true);
            }
        });
    }
}
