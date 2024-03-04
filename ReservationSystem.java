import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservationSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ticketdetails";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    private static Connection connection;

    private static JFrame mainFrame;
    private static JTextArea resultArea;
    private static JTable reservationTable;
    private static DefaultTableModel tableModel;
    private static ArrayList<Reservation> reservations = new ArrayList<>();

    private static final String[] TRAIN_NUMBERS = {"12198", "12307", "18189", "12308", "22926", "22688", "12628", "22691"};
    private static final String[] TRAIN_NAMES = {"Bhopal InterCity Express ", "Jodhpur Superfast Express", "Ernakulam Express", "Howrah Superfast Express",
            "Vande bharat", "Mysore Express", "Karnataka Express", "Rajdhani Express"};

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private static final int MIN_PNR = 1000;
    private static final int MAX_PNR = 9999;

    private static JFrame viewReservationFrame;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Class.forName("com.mysql.cj.jdbc.Driver");
            SwingUtilities.invokeLater(ReservationSystem::createLogin);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
        }
    }

    private static void createLogin() {
    JFrame loginFrame = new JFrame("Login Form");
    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    loginFrame.setSize(300, 200); // Adjusted size for better layout

    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(10, 10, 10, 10); // Increased insets for better spacing

    JTextField usernameField = new JTextField(15);
    JPasswordField passwordField = new JPasswordField(15);
    JButton loginButton = new JButton("Login");
    JButton createAccountButton = new JButton("Create Account");

    loginPanel.add(new JLabel("Username:"), gbc);
    gbc.gridx++;
    loginPanel.add(usernameField, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    loginPanel.add(new JLabel("Password:"), gbc);
    gbc.gridx++;
    loginPanel.add(passwordField, gbc);
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER; 
    loginPanel.add(loginButton, gbc);
    gbc.gridy++;
    loginPanel.add(createAccountButton, gbc);

    
    Dimension buttonSize = new Dimension(120, 30);
    loginButton.setPreferredSize(buttonSize);
    createAccountButton.setPreferredSize(buttonSize);

    loginButton.addActionListener(e -> {
        if (validLogin(usernameField.getText(), new String(passwordField.getPassword()))) {
            loginFrame.dispose();
            createMain();
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Invalid login credentials. Try again.");
        }
    });

    createAccountButton.addActionListener(e -> {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Username and password cannot be empty.");
        } else {
            createUser(username, password);
        }
    });

    loginFrame.getContentPane().add(loginPanel);
    loginFrame.pack();
    loginFrame.setLocationRelativeTo(null);
    loginFrame.setVisible(true);
}


    private static void createUser(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Account created successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create account.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to create account.");
        }
    }

    private static boolean validLogin(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to validate login credentials.");
        }
        return false;
    }

    private static void createMain() {
        mainFrame = new JFrame("Train Reservation System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        ImageIcon insertIcon = new ImageIcon("insert_icon.png");
        Image scaledInsertIcon = insertIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton insertButton = new JButton("Insert Record", new ImageIcon(scaledInsertIcon));

        ImageIcon cancelIcon = new ImageIcon("cancel_icon.png");
        Image scaledCancelIcon = cancelIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton cancelReservationButton = new JButton("Cancel Reservation", new ImageIcon(scaledCancelIcon));

        JButton viewReservationButton = new JButton("View Reservations");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField passengerNameField = new JTextField(15);
        JComboBox<String> trainNumberComboBox = new JComboBox<>(TRAIN_NUMBERS);
        JTextField trainNameField = new JTextField(15);
        trainNumberComboBox.addActionListener(e -> {
            int selectedIndex = trainNumberComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < TRAIN_NAMES.length) {
                trainNameField.setText(TRAIN_NAMES[selectedIndex]);
            }
        });
        JComboBox<String> classTypeComboBox = new JComboBox<>(new String[]{"First Class", "Second Class", "Third Class", "Sleeper"});
        JTextField journeyDateField = new JTextField(15);
        JTextField fromField = new JTextField(15);
        JTextField toField = new JTextField(15);

        gbc.gridwidth = 1;
        inputPanel.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx++;
        inputPanel.add(passengerNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Train Number:"), gbc);
        gbc.gridx++;
        inputPanel.add(trainNumberComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Train Name:"), gbc);
        gbc.gridx++;
        inputPanel.add(trainNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Class Type:"), gbc);
        gbc.gridx++;
        inputPanel.add(classTypeComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Journey Date:"), gbc);
        gbc.gridx++;
        inputPanel.add(journeyDateField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("(Date Format: yyyy-mm-dd)"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("From:"), gbc);
        gbc.gridx++;
        inputPanel.add(fromField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("To:"), gbc);
        gbc.gridx++;
        inputPanel.add(toField, gbc);

        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add tableScrollPane instead of scrollPane
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(insertButton);
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(viewReservationButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        insertButton.addActionListener(e -> insertRecord(passengerNameField, trainNumberComboBox, classTypeComboBox, journeyDateField, fromField, toField));
        cancelReservationButton.addActionListener(e -> cancelReservation());
        viewReservationButton.addActionListener(e -> viewReservations());
    }

    private static void viewReservations() {
        // Creating new JFrame to display reservations
        viewReservationFrame = new JFrame("View Reservations");
        viewReservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewReservationFrame.setSize(600, 400);

        JPanel viewReservationPanel = new JPanel();
        viewReservationPanel.setLayout(new BorderLayout());

        // Table for displaying reservations
        JTable viewReservationTable = new JTable();
        DefaultTableModel viewTableModel = new DefaultTableModel();
        viewTableModel.addColumn("PNR");
        viewTableModel.addColumn("Passenger Name");
        viewTableModel.addColumn("Train Number");
        viewTableModel.addColumn("Class Type");
        viewTableModel.addColumn("Journey Date");
        viewTableModel.addColumn("From");
        viewTableModel.addColumn("To");
        viewReservationTable.setModel(viewTableModel);

        JScrollPane tableScrollPane = new JScrollPane(viewReservationTable); // Scrollable table

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM reservations";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear previous data from the table model
            viewTableModel.setRowCount(0);

            while (resultSet.next()) {
                int pnr = resultSet.getInt("pnr_number");
                String passengerName = resultSet.getString("passenger_name");
                String trainNumber = resultSet.getString("train_number");
                String classType = resultSet.getString("class_type");
                String journeyDate = resultSet.getString("journey_date");
                String from = resultSet.getString("departure_from");
                String to = resultSet.getString("destination_to");

                // Add row to the table model
                viewTableModel.addRow(new Object[]{pnr, passengerName, trainNumber, classType, journeyDate, from, to});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(viewReservationFrame, "Failed to retrieve reservations.");
        }

        viewReservationPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button to go back to train reservation system panel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            viewReservationFrame.dispose();
            mainFrame.setVisible(true);
        });
        viewReservationPanel.add(backButton, BorderLayout.SOUTH);

        viewReservationFrame.setLocationRelativeTo(mainFrame);
        viewReservationFrame.add(viewReservationPanel);
        viewReservationFrame.setVisible(true);
    }

    private static void insertRecord(JTextField passengerNameField, JComboBox<String> trainNumberComboBox,
                                     JComboBox<String> classTypeComboBox, JTextField journeyDateField, JTextField fromField, JTextField toField) {
        String passengerName = passengerNameField.getText();
        String trainNumber = (String) trainNumberComboBox.getSelectedItem();
        String trainName = "";
        int selectedIndex = trainNumberComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < TRAIN_NAMES.length) {
            trainName = TRAIN_NAMES[selectedIndex];
        }
        String classType = (String) classTypeComboBox.getSelectedItem();
        String journeyDate = journeyDateField.getText();
        String from = fromField.getText();
        String to = toField.getText();

        if (passengerName.isEmpty() || trainNumber.isEmpty() || trainName.isEmpty() || classType.isEmpty() ||
                journeyDate.isEmpty() || from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "All fields must be filled.");
            return;
        }

        if (!validDate(journeyDate)) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid date format. Please use yyyy-mm-dd.");
            return;
        }

        try {
            String insertQuery = "INSERT INTO reservations (pnr_number, passenger_name, train_number, class_type, journey_date, " +
                    "departure_from, destination_to) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            int pnr = generateRandomPnrNumber();
            preparedStatement.setInt(1, pnr);
            preparedStatement.setString(2, passengerName);
            preparedStatement.setString(3, trainNumber);
            preparedStatement.setString(4, classType);
            preparedStatement.setString(5, journeyDate);
            preparedStatement.setString(6, from);
            preparedStatement.setString(7, to);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Display confirmation message with generated PNR
                JOptionPane.showMessageDialog(mainFrame, "Record added successfully. PNR: " + pnr);
                resultArea.append("Record added successfully. PNR: " + pnr + "\n");
                // Add the reservation to the ArrayList
                reservations.add(new Reservation(pnr, passengerName, trainNumber, trainName, classType, journeyDate, from, to));
            } else {
                resultArea.append("Failed to add record.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Failed to add record.");
        }

        // Clear input fields
        passengerNameField.setText("");
        trainNumberComboBox.setSelectedIndex(0);
        classTypeComboBox.setSelectedIndex(0);
        journeyDateField.setText("");
        fromField.setText("");
        toField.setText("");
    }

    private static int generateRandomPnrNumber() {
        Random random = new Random();
        return random.nextInt(MAX_PNR - MIN_PNR + 1) + MIN_PNR;
    }

    private static boolean validDate(String date) {
        Matcher matcher = DATE_PATTERN.matcher(date);
        return matcher.matches();
    }

    private static void cancelReservation() {
        String pnrString = JOptionPane.showInputDialog(mainFrame, "Enter PNR number to cancel:");

        if (pnrString != null && !pnrString.isEmpty()) {
            try {
                int pnrNumber = Integer.parseInt(pnrString);
                // Fetch reservation details directly from the database
                Reservation reservation = fetchReservationFromDatabase(pnrNumber);

                if (reservation != null) {
                    showReservationDetails(reservation);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "No reservation found with the provided PNR number.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid PNR format. Please enter a valid number.");
            }
        }
    }

    private static Reservation fetchReservationFromDatabase(int pnrNumber) {
        try {
            String query = "SELECT * FROM reservations WHERE pnr_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, pnrNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int pnr = resultSet.getInt("pnr_number");
                String passengerName = resultSet.getString("passenger_name");
                String trainNumber = resultSet.getString("train_number");
                String classType = resultSet.getString("class_type");
                String journeyDate = resultSet.getString("journey_date");
                String from = resultSet.getString("departure_from");
                String to = resultSet.getString("destination_to");

                return new Reservation(pnr, passengerName, trainNumber, "", classType, journeyDate, from, to);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Failed to fetch reservation from the database.");
        }
        return null;
    }

    private static void showReservationDetails(Reservation reservation) {
        int option = JOptionPane.showConfirmDialog(mainFrame,
                "PNR: " + reservation.getPnr() + "\n" +
                        "Passenger Name: " + reservation.getPassengerName() + "\n" +
                        "Train Number: " + reservation.getTrainNumber() + "\n" +
                        "Train Name: " + reservation.getTrainName() + "\n" +
                        "Class Type: " + reservation.getClassType() + "\n" +
                        "Journey Date: " + reservation.getJourneyDate() + "\n" +
                        "From: " + reservation.getFrom() + "\n" +
                        "To: " + reservation.getTo() + "\n\n" +
                        "Do you want to cancel this reservation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteReservation(reservation);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Reservation was not canceled.");
        }
    }

    private static void deleteReservation(Reservation reservation) {
        try {
            String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, reservation.getPnr());

            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                resultArea.append("Reservation with PNR " + reservation.getPnr() + " canceled.\n");
                reservations.remove(reservation);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to cancel reservation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Failed to cancel reservation.");
        }
    }

    private static class Reservation {
        private int pnr;
        private String passengerName;
        private String trainNumber;
        private String trainName;
        private String classType;
        private String journeyDate;
        private String from;
        private String to;

        public Reservation(int pnr, String passengerName, String trainNumber, String trainName, String classType, String journeyDate, String from, String to) {
            this.pnr = pnr;
            this.passengerName = passengerName;
            this.trainNumber = trainNumber;
            this.trainName = trainName;
            this.classType = classType;
            this.journeyDate = journeyDate;
            this.from = from;
            this.to = to;
        }

        public int getPnr() {
            return pnr;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public String getTrainNumber() {
            return trainNumber;
        }

        public String getTrainName() {
            return trainName;
        }

        public String getClassType() {
            return classType;
        }

        public String getJourneyDate() {
            return journeyDate;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}
