package viewAndController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.MainApplication;
import util.DatabaseConnector;
import model.Car;

public class CarDataPanel extends JPanel {
    private MainApplication parentFrame;
    private DatabaseConnector databaseConnector;

    // Components for CRUD operations
    private JTextField modelField;
    private JTextField manufacturerField;
    private JTextField yearField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton; // New back button
    private JTable carTable;
    private DefaultTableModel tableModel;

    public CarDataPanel(MainApplication parent, DatabaseConnector connector) {
        this.parentFrame = parent;
        this.databaseConnector = connector;
        initComponents();
        loadCars();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x102031)); // Set full background color to 0x102031

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(0x102031)); // Set full background color to 0x102031
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setForeground(Color.WHITE); // Set label text color
        modelField = new JTextField();
        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        manufacturerLabel.setForeground(Color.WHITE); // Set label text color
        manufacturerField = new JTextField();
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setForeground(Color.WHITE); // Set label text color
        yearField = new JTextField();

        formPanel.add(modelLabel);
        formPanel.add(modelField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(yearLabel);
        formPanel.add(yearField);

        addButton = new JButton("Add");
        addButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        addButton.setForeground(Color.WHITE); // Set button text color
        addButton.addActionListener(e -> addCar());
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        updateButton.setForeground(Color.WHITE); // Set button text color
        updateButton.addActionListener(e -> updateCar());
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        deleteButton.setForeground(Color.WHITE); // Set button text color
        deleteButton.addActionListener(e -> deleteCar());

        // Initialize back button
        backButton = new JButton("Back");
        backButton.setBackground(new Color(0x204060)); // Set button background color to 0x204060
        backButton.setForeground(Color.WHITE); // Set button text color
        backButton.addActionListener(e -> parentFrame.showMenuPanel()); // Navigate back to menu panel

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

        tableModel = new DefaultTableModel(new String[]{"Model", "Manufacturer", "Year"}, 0);
        carTable = new JTable(tableModel);
        carTable.setBackground(new Color(0x102031)); // Set table background color to 0x102031
        carTable.setForeground(Color.WHITE); // Set table text color to white
        JScrollPane scrollPane = new JScrollPane(carTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        carTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow != -1) {
                modelField.setText((String) tableModel.getValueAt(selectedRow, 0));
                manufacturerField.setText((String) tableModel.getValueAt(selectedRow, 1));
                yearField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
            }
        });
    }

    private void loadCars() {
        List<Car> cars = fetchCarsFromDatabase();
        tableModel.setRowCount(0);
        for (Car car : cars) {
            tableModel.addRow(new Object[]{car.getModel(), car.getManufacturer(), car.getYear()});
        }
    }

    private List<Car> fetchCarsFromDatabase() {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Cars")) {
            while (rs.next()) {
                String model = rs.getString("Model");
                String manufacturer = rs.getString("Manufacturer");
                int year = rs.getInt("Year");
                Car car = new Car(model, manufacturer, year);
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    private void addCar() {
        String model = modelField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();
        String yearStr = yearField.getText().trim();

        if (model.isEmpty() || manufacturer.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            Car newCar = new Car(model, manufacturer, year);
            saveCarToDatabase(newCar);
            tableModel.addRow(new Object[]{newCar.getModel(), newCar.getManufacturer(), newCar.getYear()});
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String model = modelField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();
        String yearStr = yearField.getText().trim();

        if (model.isEmpty() || manufacturer.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            Car selectedCar = new Car(model, manufacturer, year);
            updateCarInDatabase(selectedCar);

            tableModel.setValueAt(selectedCar.getModel(), selectedRow, 0);
            tableModel.setValueAt(selectedCar.getManufacturer(), selectedRow, 1);
            tableModel.setValueAt(selectedCar.getYear(), selectedRow, 2);

            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String model = (String) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteCarFromDatabase(model);
            tableModel.removeRow(selectedRow);
            clearFields();
        }
    }

    private void saveCarToDatabase(Car car) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Cars (Model, Manufacturer, Year) VALUES (?, ?, ?)")) {
            stmt.setString(1, car.getModel());
            stmt.setString(2, car.getManufacturer());
            stmt.setInt(3, car.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCarInDatabase(Car car) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Cars SET Manufacturer=?, Year=? WHERE Model=?")) {
            stmt.setString(1, car.getManufacturer());
            stmt.setInt(2, car.getYear());
            stmt.setString(3, car.getModel());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCarFromDatabase(String model) {
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Cars WHERE Model=?")) {
            stmt.setString(1, model);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearFields() {
        modelField.setText("");
        manufacturerField.setText("");
        yearField.setText("");
        carTable.clearSelection();
    }
}
