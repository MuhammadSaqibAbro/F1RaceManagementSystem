package viewAndController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnector;
import model.Driver;

public class DriverInfoPanel extends JPanel {
    private DatabaseConnector databaseConnector;

    // Components for CRUD operations
    private JTextField nameField;
    private JTextField ageField;
    private JTextField nationalityField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable driverTable;
    private DefaultTableModel tableModel;
    private JButton backButton; // Back button for navigation

    private JPanel menuPanel; // Reference to the menu panel for navigation

    public DriverInfoPanel(DatabaseConnector connector, JPanel menuPanel) {
        this.databaseConnector = connector;
        this.menuPanel = menuPanel; // Set reference to the menu panel
        initComponents();
        loadDrivers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set full background color to 0x102031

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(new Color(0x102031)); // Set full background color to 0x102031
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE); // Set label text color
        nameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setForeground(Color.WHITE); // Set label text color
        ageField = new JTextField();
        JLabel nationalityLabel = new JLabel("Nationality:");
        nationalityLabel.setForeground(Color.WHITE); // Set label text color
        nationalityField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(nationalityLabel);
        formPanel.add(nationalityField);

        addButton = new JButton("Add");
        addButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        addButton.setForeground(Color.WHITE); // Set button text color
        addButton.addActionListener(e -> addDriver());
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        updateButton.setForeground(Color.WHITE); // Set button text color
        updateButton.addActionListener(e -> updateDriver());
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        deleteButton.setForeground(Color.WHITE); // Set button text color
        deleteButton.addActionListener(e -> deleteDriver());

        backButton = new JButton("Back"); // Initialize back button
        backButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        backButton.setForeground(Color.WHITE); // Set button text color
        backButton.addActionListener(e -> backToMenu()); // Add action listener for back button

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x102031)); // Set full background color to 0x102031
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton); // Add back button to the button panel

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x204060)); // Set header panel color to 0x204060
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"Name", "Age", "Nationality"}, 0);
        driverTable = new JTable(tableModel);
        driverTable.setBackground(new Color(0x102031)); // Set table background color to 0x102031
        driverTable.setForeground(Color.WHITE); // Set table text color to white
        JScrollPane scrollPane = new JScrollPane(driverTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        driverTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = driverTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText((String) tableModel.getValueAt(selectedRow, 0));
                ageField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                nationalityField.setText((String) tableModel.getValueAt(selectedRow, 2));
            }
        });
    }

    private void loadDrivers() {
        List<Driver> drivers = fetchDriversFromDatabase();
        tableModel.setRowCount(0);
        for (Driver driver : drivers) {
            tableModel.addRow(new Object[]{driver.getName(), driver.getAge(), driver.getNationality()});
        }
    }

    private List<Driver> fetchDriversFromDatabase() {
        List<Driver> drivers = new ArrayList<>();
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Drivers")) {
            while (rs.next()) {
                String name = rs.getString("Name");
                int age = rs.getInt("Age");
                String nationality = rs.getString("Nationality");
                Driver driver = new Driver(name, age, nationality);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    private void addDriver() {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String nationality = nationalityField.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || nationality.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            Driver newDriver = new Driver(name, age, nationality);
            saveDriverToDatabase(newDriver);
            tableModel.addRow(new Object[]{newDriver.getName(), newDriver.getAge(), newDriver.getNationality()});
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDriver() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a driver to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String nationality = nationalityField.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || nationality.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            Driver selectedDriver = new Driver(name, age, nationality);
            updateDriverInDatabase(selectedDriver);

            tableModel.setValueAt(selectedDriver.getName(), selectedRow, 0);
            tableModel.setValueAt(selectedDriver.getAge(), selectedRow, 1);
            tableModel.setValueAt(selectedDriver.getNationality(), selectedRow, 2);

            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDriver() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a driver to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this driver?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteDriverFromDatabase(name);
            tableModel.removeRow(selectedRow);
            clearFields();
        }
    }

    private void saveDriverToDatabase(Driver driver) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Drivers (Name, Age, Nationality) VALUES (?, ?, ?)")) {
            stmt.setString(1, driver.getName());
            stmt.setInt(2, driver.getAge());
            stmt.setString(3, driver.getNationality());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDriverInDatabase(Driver driver) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Drivers SET Age=?, Nationality=? WHERE Name=?")) {
            stmt.setInt(1, driver.getAge());
            stmt.setString(2, driver.getNationality());
            stmt.setString(3, driver.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteDriverFromDatabase(String name) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Drivers WHERE Name=?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        nationalityField.setText("");
        driverTable.clearSelection();
    }

    private void backToMenu() {
        // Switch to the menu panel
        CardLayout cardLayout = (CardLayout) menuPanel.getLayout();
        cardLayout.show(menuPanel, "Menu");
    }
}
