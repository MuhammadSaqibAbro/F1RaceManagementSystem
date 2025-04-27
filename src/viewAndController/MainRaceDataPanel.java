package viewAndController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.MainApplication;
import model.MainRaceData;
import util.DatabaseConnector;

public class MainRaceDataPanel extends JPanel {

    private MainApplication parentFrame;
    private DatabaseConnector databaseConnector;

    // Components for CRUD operations
    private JTextField circuitField;
    private JTextField lapsField;
    private JTextField conditionsField;
    private JTextField numberOfDriversField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton; // Added back button
    private JTable raceDataTable;

    public MainRaceDataPanel(MainApplication parent, DatabaseConnector connector) {
        this.parentFrame = parent;
        this.databaseConnector = connector;
        initComponents();
        loadRaceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set full background color to 0x102031

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(new Color(0x102031)); // Set full background color to 0x102031
        JLabel circuitLabel = new JLabel("Circuit:");
        circuitLabel.setForeground(Color.WHITE); // Set label text color
        circuitField = new JTextField();
        JLabel lapsLabel = new JLabel("Laps:");
        lapsLabel.setForeground(Color.WHITE); // Set label text color
        lapsField = new JTextField();
        JLabel conditionsLabel = new JLabel("Conditions:");
        conditionsLabel.setForeground(Color.WHITE); // Set label text color
        conditionsField = new JTextField();
        JLabel numberOfDriversLabel = new JLabel("Number of Drivers:");
        numberOfDriversLabel.setForeground(Color.WHITE); // Set label text color
        numberOfDriversField = new JTextField();

        formPanel.add(circuitLabel);
        formPanel.add(circuitField);
        formPanel.add(lapsLabel);
        formPanel.add(lapsField);
        formPanel.add(conditionsLabel);
        formPanel.add(conditionsField);
        formPanel.add(numberOfDriversLabel);
        formPanel.add(numberOfDriversField);

        addButton = new JButton("Add");
        addButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        addButton.setForeground(Color.WHITE); // Set button text color
        addButton.addActionListener(e -> addRaceData());
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        updateButton.setForeground(Color.WHITE); // Set button text color
        updateButton.addActionListener(e -> updateRaceData());
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        deleteButton.setForeground(Color.WHITE); // Set button text color
        deleteButton.addActionListener(e -> deleteRaceData());
        backButton = new JButton("Back"); // Initialize back button
        backButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        backButton.setForeground(Color.WHITE); // Set button text color
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement navigation action here, e.g., switch to main menu or previous frame
                parentFrame.showMenuPanel(); // Example method to switch to main menu
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x102031)); // Set full background color to 0x102031
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton); // Add back button to button panel

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x204060)); // Set header panel color to 0x204060
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create table model with column names
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"Circuit", "Laps", "Conditions", "Number of Drivers"}, 0);

        raceDataTable = new JTable(tableModel);
        raceDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        raceDataTable.setBackground(new Color(0x102031)); // Set table background color to 0x102031
        raceDataTable.setForeground(Color.WHITE); // Set table text color to white

        JScrollPane scrollPane = new JScrollPane(raceDataTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add list selection listener to populate fields when selection changes
        raceDataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = raceDataTable.getSelectedRow();
                if (selectedRow != -1) {
                    String circuit = (String) raceDataTable.getValueAt(selectedRow, 0);
                    String laps = String.valueOf(raceDataTable.getValueAt(selectedRow, 1));
                    String conditions = (String) raceDataTable.getValueAt(selectedRow, 2);
                    String numberOfDrivers = String.valueOf(raceDataTable.getValueAt(selectedRow, 3));

                    circuitField.setText(circuit);
                    lapsField.setText(laps);
                    conditionsField.setText(conditions);
                    numberOfDriversField.setText(numberOfDrivers);
                }
            }
        });
    }

    private void loadRaceData() {
        List<MainRaceData> raceData = fetchRaceDataFromDatabase();
        DefaultTableModel model = (DefaultTableModel) raceDataTable.getModel();
        model.setRowCount(0); // Clear existing rows

        for (MainRaceData data : raceData) {
            model.addRow(new Object[]{data.getCircuit(), data.getLaps(), data.getConditions(), data.getNumberOfDrivers()});
        }
    }

    private List<MainRaceData> fetchRaceDataFromDatabase() {
        List<MainRaceData> raceData = new ArrayList<>();
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM MainRaceData")) {

            while (rs.next()) {
                String circuit = rs.getString("Circuit");
                int laps = rs.getInt("Laps");
                String conditions = rs.getString("Conditions");
                int numberOfDrivers = rs.getInt("NumberOfDrivers");

                MainRaceData data = new MainRaceData(circuit, laps, conditions, numberOfDrivers);
                raceData.add(data);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while fetching race data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return raceData;
    }

    private void addRaceData() {
        String circuit = circuitField.getText().trim();
        String lapsStr = lapsField.getText().trim();
        String conditions = conditionsField.getText().trim();
        String numberOfDriversStr = numberOfDriversField.getText().trim();

        if (circuit.isEmpty() || lapsStr.isEmpty() || conditions.isEmpty() || numberOfDriversStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int laps = Integer.parseInt(lapsStr);
            int numberOfDrivers = Integer.parseInt(numberOfDriversStr);
            MainRaceData newRaceData = new MainRaceData(circuit, laps, conditions, numberOfDrivers);

            try (Connection conn = databaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO MainRaceData (Circuit, Laps, Conditions, NumberOfDrivers) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, circuit);
                stmt.setInt(2, laps);
                stmt.setString(3, conditions);
                stmt.setInt(4, numberOfDrivers);
                stmt.executeUpdate();
            }

            DefaultTableModel model = (DefaultTableModel) raceDataTable.getModel();
            model.addRow(new Object[]{circuit, laps, conditions, numberOfDrivers});
            clearFields();
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format or database error.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateRaceData() {
        int selectedRow = raceDataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a race data entry to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String circuit = circuitField.getText().trim();
        String lapsStr = lapsField.getText().trim();
        String conditions = conditionsField.getText().trim();
        String numberOfDriversStr = numberOfDriversField.getText().trim();

        if (circuit.isEmpty() || lapsStr.isEmpty() || conditions.isEmpty() || numberOfDriversStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int laps = Integer.parseInt(lapsStr);
            int numberOfDrivers = Integer.parseInt(numberOfDriversStr);

            raceDataTable.setValueAt(circuit, selectedRow, 0);
            raceDataTable.setValueAt(laps, selectedRow, 1);
            raceDataTable.setValueAt(conditions, selectedRow, 2);
            raceDataTable.setValueAt(numberOfDrivers, selectedRow, 3);

            try (Connection conn = databaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE MainRaceData SET Circuit=?, Laps=?, Conditions=?, NumberOfDrivers=? WHERE Circuit=?")) {
                stmt.setString(1, circuit);
                stmt.setInt(2, laps);
                stmt.setString(3, conditions);
                stmt.setInt(4, numberOfDrivers);
                stmt.setString(5, (String) raceDataTable.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
            }

            clearFields();
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format or database error.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteRaceData() {
        int selectedRow = raceDataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a race data entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this race data entry?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try (Connection conn = databaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM MainRaceData WHERE Circuit=?")) {
                stmt.setString(1, (String) raceDataTable.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error while deleting race data.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            DefaultTableModel model = (DefaultTableModel) raceDataTable.getModel();
            model.removeRow(selectedRow);
            clearFields();
        }
    }

    private void clearFields() {
        circuitField.setText("");
        lapsField.setText("");
        conditionsField.setText("");
        numberOfDriversField.setText("");
        raceDataTable.clearSelection();
    }
}
