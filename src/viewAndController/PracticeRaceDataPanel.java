package viewAndController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.MainApplication;
import model.PracticeRaceData;
import util.DatabaseConnector;

public class PracticeRaceDataPanel extends JPanel {

    private MainApplication parentFrame;
    private DatabaseConnector databaseConnector;

    // Components for CRUD operations
    private JTextField circuitField;
    private JTextField lapsField;
    private JTextField conditionsField;
    private JTextField sessionTypeField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton; // Added back button
    private JTable raceDataTable;
    private DefaultTableModel tableModel;

    public PracticeRaceDataPanel(MainApplication parent, DatabaseConnector connector) {
        this.parentFrame = parent;
        this.databaseConnector = connector;
        initComponents();
        loadRaceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set full background color to 0x102031

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
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
        JLabel sessionTypeLabel = new JLabel("Session Type:");
        sessionTypeLabel.setForeground(Color.WHITE); // Set label text color
        sessionTypeField = new JTextField();

        formPanel.add(circuitLabel);
        formPanel.add(circuitField);
        formPanel.add(lapsLabel);
        formPanel.add(lapsField);
        formPanel.add(conditionsLabel);
        formPanel.add(conditionsField);
        formPanel.add(sessionTypeLabel);
        formPanel.add(sessionTypeField);

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

        // Table setup
        String[] columnNames = {"Circuit", "Laps", "Conditions", "Session Type"};
        tableModel = new DefaultTableModel(columnNames, 0);
        raceDataTable = new JTable(tableModel);
        raceDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        raceDataTable.setBackground(new Color(0x102031)); // Set list background color to 0x102031
        raceDataTable.setForeground(Color.WHITE); // Set list text color to white
        raceDataTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = raceDataTable.getSelectedRow();
            if (selectedRow != -1) {
                circuitField.setText((String) tableModel.getValueAt(selectedRow, 0));
                lapsField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                conditionsField.setText((String) tableModel.getValueAt(selectedRow, 2));

                sessionTypeField.setText((String) tableModel.getValueAt(selectedRow, 3));
            }
        });

        JScrollPane scrollPane = new JScrollPane(raceDataTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRaceData() {
        List<PracticeRaceData> raceData = fetchRaceDataFromDatabase();
        tableModel.setRowCount(0); // Clear existing data
        for (PracticeRaceData data : raceData) {
            tableModel.addRow(new Object[]{data.getCircuit(), data.getLaps(), data.getConditions(), data.getSessionType()});
        }
    }

    private List<PracticeRaceData> fetchRaceDataFromDatabase() {
        List<PracticeRaceData> raceData = new ArrayList<>();
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM PracticeRaceData")) {

            while (rs.next()) {
                String circuit = rs.getString("Circuit");
                int laps = rs.getInt("Laps");
                String conditions = rs.getString("Conditions");
                String sessionType = rs.getString("SessionType");

                PracticeRaceData data = new PracticeRaceData(circuit, laps, conditions, sessionType);
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
        String sessionType = sessionTypeField.getText().trim();

        if (circuit.isEmpty() || lapsStr.isEmpty() || conditions.isEmpty() || sessionType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int laps = Integer.parseInt(lapsStr);
            PracticeRaceData newRaceData = new PracticeRaceData(circuit, laps, conditions, sessionType);

            try (Connection conn = databaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO PracticeRaceData (Circuit, Laps, Conditions, SessionType) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, circuit);
                stmt.setInt(2, laps);
                stmt.setString(3, conditions);
                stmt.setString(4, sessionType);
                stmt.executeUpdate();
            }

            tableModel.addRow(new Object[]{circuit, laps, conditions, sessionType});
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
        String sessionType = sessionTypeField.getText().trim();

        if (circuit.isEmpty() || lapsStr.isEmpty() || conditions.isEmpty() || sessionType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int laps = Integer.parseInt(lapsStr);

            try (Connection conn = databaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE PracticeRaceData SET Circuit=?, Laps=?, Conditions=?, SessionType=? WHERE Circuit=?")) {
                stmt.setString(1, circuit);
                stmt.setInt(2, laps);
                stmt.setString(3, conditions);
                stmt.setString(4, sessionType);
                stmt.setString(5, (String) tableModel.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
            }

            tableModel.setValueAt(circuit, selectedRow, 0);
            tableModel.setValueAt(laps, selectedRow, 1);
            tableModel.setValueAt(conditions, selectedRow, 2);
            tableModel.setValueAt(sessionType, selectedRow, 3);
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
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM PracticeRaceData WHERE Circuit=?")) {
                stmt.setString(1, (String) tableModel.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error while deleting race data.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            tableModel.removeRow(selectedRow);
            clearFields();
        }
    }

    private void clearFields() {
        circuitField.setText("");
        lapsField.setText("");
        conditionsField.setText("");
        sessionTypeField.setText("");
        raceDataTable.clearSelection();
    }
}
