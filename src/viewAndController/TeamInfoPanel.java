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
import model.Team;
import util.DatabaseConnector;

public class TeamInfoPanel extends JPanel {

    private MainApplication parentFrame;
    private DatabaseConnector databaseConnector;

    // Components for CRUD operations
    private JTextField nameField;
    private JTextField countryField;
    private JTextField managerField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton; // Back button declaration
    private JTable teamTable;
    private DefaultTableModel tableModel;

    public TeamInfoPanel(MainApplication parent, DatabaseConnector connector) {
        this.parentFrame = parent;
        this.databaseConnector = connector;
        initComponents();
        loadTeams();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set full background color to dark blue

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(0x102031)); // Set full background color to dark blue
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE); // Set label text color to white
        nameField = new JTextField();
        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setForeground(Color.WHITE); // Set label text color to white
        countryField = new JTextField();
        JLabel managerLabel = new JLabel("Manager:");
        managerLabel.setForeground(Color.WHITE); // Set label text color to white
        managerField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(countryLabel);
        formPanel.add(countryField);
        formPanel.add(managerLabel);
        formPanel.add(managerField);

        addButton = new JButton("Add");
        addButton.setBackground(new Color(0x34637A)); // Set button background color to a lighter blue
        addButton.setForeground(Color.WHITE); // Set button text color to white
        addButton.addActionListener(e -> addTeam());
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0x34637A)); // Set button background color to a lighter blue
        updateButton.setForeground(Color.WHITE); // Set button text color to white
        updateButton.addActionListener(e -> updateTeam());
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0x34637A)); // Set button background color to a lighter blue
        deleteButton.setForeground(Color.WHITE); // Set button text color to white
        deleteButton.addActionListener(e -> deleteTeam());

        // Back button initialization
        backButton = new JButton("Back");
        backButton.setBackground(new Color(0x34637A)); // Set button background color to a lighter blue
        backButton.setForeground(Color.WHITE); // Set button text color to white
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showMenuPanel(); // Replace with your method to show the main menu
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x102031)); // Set button panel background color to dark blue
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton); // Add the back button to the panel

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x204060)); // Set header panel color to a lighter blue
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table setup
        String[] columnNames = {"Name", "Country", "Manager"};
        tableModel = new DefaultTableModel(columnNames, 0);
        teamTable = new JTable(tableModel);
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.setBackground(new Color(0x102031)); // Set table background color to dark blue
        teamTable.setForeground(Color.WHITE); // Set table text color to white
        teamTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = teamTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText((String) tableModel.getValueAt(selectedRow, 0));
                countryField.setText((String) tableModel.getValueAt(selectedRow, 1));
                managerField.setText((String) tableModel.getValueAt(selectedRow, 2));
            }
        });

        JScrollPane scrollPane = new JScrollPane(teamTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTeams() {
        List<Team> teams = fetchTeamsFromDatabase();
        tableModel.setRowCount(0); // Clear existing data
        for (Team team : teams) {
            tableModel.addRow(new Object[]{team.getName(), team.getCountry(), team.getManager()});
        }
    }

    private List<Team> fetchTeamsFromDatabase() {
        List<Team> teams = new ArrayList<>();
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Teams")) {

            while (rs.next()) {
                String name = rs.getString("Name");
                String country = rs.getString("Country");
                String manager = rs.getString("Manager");
                Team team = new Team(name, country, manager);
                teams.add(team);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching teams from database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return teams;
    }

    private void addTeam() {
        String name = nameField.getText().trim();
        String country = countryField.getText().trim();
        String manager = managerField.getText().trim();

        if (name.isEmpty() || country.isEmpty() || manager.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Team newTeam = new Team(name, country, manager);
        if (saveTeamToDatabase(newTeam)) {
            tableModel.addRow(new Object[]{newTeam.getName(), newTeam.getCountry(), newTeam.getManager()});
            clearFields();
        }
    }

    private boolean saveTeamToDatabase(Team team) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Teams (Name, Country, Manager) VALUES (?, ?, ?)")) {
            stmt.setString(1, team.getName());
            stmt.setString(2, team.getCountry());
            stmt.setString(3, team.getManager());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving team to database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void updateTeam() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a team to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String country = countryField.getText().trim();
        String manager = managerField.getText().trim();

        if (name.isEmpty() || country.isEmpty() || manager.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Team team = new Team(name, country, manager);
        if (updateTeamInDatabase(team)) {
            tableModel.setValueAt(name, selectedRow, 0);
            tableModel.setValueAt(country, selectedRow, 1);
            tableModel.setValueAt(manager, selectedRow, 2);
            clearFields();
        }
    }

    private boolean updateTeamInDatabase(Team team) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Teams SET Country=?, Manager=? WHERE Name=?")) {
            stmt.setString(1, team.getCountry());
            stmt.setString(2, team.getManager());
            stmt.setString(3, team.getName());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating team in database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void deleteTeam() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a team to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this team?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            if (deleteTeamFromDatabase(name)) {
                tableModel.removeRow(selectedRow);
                clearFields();
            }
        }
    }

    private boolean deleteTeamFromDatabase(String name) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Teams WHERE Name=?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting team from database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void clearFields() {
        nameField.setText("");
        countryField.setText("");
        managerField.setText("");
    }
}
