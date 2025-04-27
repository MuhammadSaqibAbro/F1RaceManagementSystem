package main;

import javax.swing.*;
import java.awt.*;
import util.DatabaseConnector;
import viewAndController.*;

public class MainApplication extends JFrame {
    private DatabaseConnector connector;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private CarDataPanel carDataPanel; // Declare instance variable for CarDataPanel

    // Constructor
    public MainApplication() {
        this.connector = new DatabaseConnector();
        initializeUI();
    }

    // Initialize UI method
    private void initializeUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout) {
            // Override paintComponent to draw the background image
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the background image
                ImageIcon backgroundImage = new ImageIcon("main/f1.jpg");
                // Draw the background image
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Initialize panels
        DriverInfoPanel driverInfoPanel = new DriverInfoPanel(connector, mainPanel); // Update constructor call
        carDataPanel = new CarDataPanel(this, connector); // Instantiate CarDataPanel with reference to MainApplication
        MainRaceDataPanel mainRaceDataPanel = new MainRaceDataPanel(this, connector);
        PracticeRaceDataPanel practiceRaceDataPanel = new PracticeRaceDataPanel(this, connector);
        RaceDataPanel raceDataPanel = new RaceDataPanel(this, connector);
        TeamInfoPanel teamInfoPanel = new TeamInfoPanel(this, connector);
        HelpPanel helpPanel = new HelpPanel(this); // Pass reference to MainApplication

        // Add panels to main panel
        mainPanel.add(createMenuPanel(), "Menu");
        mainPanel.add(driverInfoPanel, "DriverManagement");
        mainPanel.add(carDataPanel, "CarManagement");
        mainPanel.add(mainRaceDataPanel, "MainRaceDataManagement");
        mainPanel.add(practiceRaceDataPanel, "PracticeRaceDataManagement");
        mainPanel.add(raceDataPanel, "RaceDataManagement");
        mainPanel.add(teamInfoPanel, "TeamManagement");
        mainPanel.add(helpPanel, "Help");

        // Add main panel to frame
        add(mainPanel);

        setTitle("F1 Race Management System Home Page");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Prevent resizing
    }

    // Create menu panel method
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(0x102031));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("F1 Race Management System Home Page", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0xFFFFFF)); // White color
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        buttonsPanel.setBackground(new Color(0x102031));

        JButton driverButton = new JButton("Driver Management");
        JButton carButton = new JButton("Car Management");
        JButton mainRaceButton = new JButton("Main Race Data Management");
        JButton practiceRaceButton = new JButton("Practice Race Data Management");
        JButton raceDataButton = new JButton("Race Data Management");
        JButton teamButton = new JButton("Team Management");
        JButton helpButton = new JButton("Help");

        // Set background color for buttons
        driverButton.setBackground(new Color(0x204060));
        carButton.setBackground(new Color(0x204060));
        mainRaceButton.setBackground(new Color(0x204060));
        practiceRaceButton.setBackground(new Color(0x204060));
        raceDataButton.setBackground(new Color(0x204060));
        teamButton.setBackground(new Color(0x204060));
        helpButton.setBackground(new Color(0x204060));

        // Set text color for buttons
        driverButton.setForeground(Color.WHITE);
        carButton.setForeground(Color.WHITE);
        mainRaceButton.setForeground(Color.WHITE);
        practiceRaceButton.setForeground(Color.WHITE);
        raceDataButton.setForeground(Color.WHITE);
        teamButton.setForeground(Color.WHITE);
        helpButton.setForeground(Color.WHITE);

        driverButton.addActionListener(e -> cardLayout.show(mainPanel, "DriverManagement"));
        carButton.addActionListener(e -> cardLayout.show(mainPanel, "CarManagement"));
        mainRaceButton.addActionListener(e -> cardLayout.show(mainPanel, "MainRaceDataManagement"));
        practiceRaceButton.addActionListener(e -> cardLayout.show(mainPanel, "PracticeRaceDataManagement"));
        raceDataButton.addActionListener(e -> cardLayout.show(mainPanel, "RaceDataManagement"));
        teamButton.addActionListener(e -> cardLayout.show(mainPanel, "TeamManagement"));
        helpButton.addActionListener(e -> cardLayout.show(mainPanel, "Help"));

        buttonsPanel.add(driverButton);
        buttonsPanel.add(carButton);
        buttonsPanel.add(mainRaceButton);
        buttonsPanel.add(practiceRaceButton);
        buttonsPanel.add(raceDataButton);
        buttonsPanel.add(teamButton);
        buttonsPanel.add(helpButton);

        menuPanel.add(buttonsPanel, BorderLayout.CENTER);

        return menuPanel;
    }

    // Method to show the menu panel
    public void showMenuPanel() {
        cardLayout.show(mainPanel, "Menu");
        carDataPanel.clearFields(); // Clear fields in CarDataPanel when navigating back
    }

    // Getter for database connector
    public DatabaseConnector getConnector() {
        return connector;
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater() for thread safety
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}
