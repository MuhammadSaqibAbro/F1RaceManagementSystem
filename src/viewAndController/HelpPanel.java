package viewAndController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.MainApplication;

public class HelpPanel extends JPanel {

    private MainApplication parentFrame; // Reference to MainApplication

    public HelpPanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame; // Set reference to parent frame
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set background color to dark blue

        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setForeground(Color.WHITE); // Set text color to white
        helpText.setBackground(new Color(0x102031)); // Set background color to dark blue
        helpText.setText("Welcome to the Help Section!\n\n" +
                "This application allows you to manage various data related to a racing event:\n" +
                "- Driver Personal Information\n" +
                "- Car Data\n" +
                "- Team Information\n" +
                "- Practice Race Data\n" +
                "- Main Race Data\n\n" +
                "Use the buttons on the left to navigate between different sections.\n" +
                "You can add, update, delete, and view details of each type of data.\n" +
                "For any assistance, please contact your administrator.\n");

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(null); // Remove border from scroll pane
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(0x34637A)); // Set button background color to a lighter blue
        backButton.setForeground(Color.WHITE); // Set button text color to white
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showMenuPanel(); // Navigate back to menu panel in MainApplication
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x102031)); // Set button panel background color to dark blue
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
