import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservationSystem {

    private static final int MIN_PNR = 1000;
    private static final int MAX_PNR = 9999;

    private static JFrame mainFrame;
    private static JTextArea resultArea;
    private static ArrayList<Reservation> reservations = new ArrayList<>();

    private static final String[] TRAIN_NUMBERS = {"12198", "12307", "18189", "12308", "22926", "22688", "12628", "22691"};
    private static final String[] TRAIN_NAMES = {"Bhopal InterCity Express ", "Jodhpur Superfast Express", "Ernakulam Express", "Howrah Superfast Express",
            "Vande bharat", "Mysore Express", "Karnataka Express", "Rajdhani Express"};

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createLogin());
    }

    private static void createLogin() {
        JFrame loginFrame = new JFrame("Login Form");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

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
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            if (ValidLogin(usernameField.getText(), new String(passwordField.getPassword()))) {
                loginFrame.dispose();
                createMain();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid login credentials. Try again.");
            }
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);
    }

    private static boolean ValidLogin(String username, String password) {
        return username.equals("root") && password.equals("login@123");
    }

    private static void createMain() {
        mainFrame = new JFrame("Train Reservation System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JButton insertButton = new JButton("Insert Record");
        JButton cancelReservationButton = new JButton("Cancel Reservation");

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

        mainPanel.add(resultArea, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(insertButton);
        buttonPanel.add(cancelReservationButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        insertButton.addActionListener(e -> insertRecord(passengerNameField, trainNumberComboBox, classTypeComboBox, journeyDateField, fromField, toField));
        cancelReservationButton.addActionListener(e -> cancelReservation());
    }

    private static void insertRecord(JTextField passengerNameField, JComboBox<String> trainNumberComboBox, JComboBox<String> classTypeComboBox, JTextField journeyDateField, JTextField fromField, JTextField toField) {
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

        if (!ValidDate(journeyDate)) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid date format. Please use yyyy-mm-dd.");
            return;
        }

        Reservation reservation = new Reservation(passengerName, trainNumber, trainName, classType, journeyDate, from, to);
        reservations.add(reservation);

        resultArea.append("Record added successfully. PNR: " + reservation.getPnr() + "\n");

        passengerNameField.setText("");
        trainNumberComboBox.setSelectedIndex(0);
        classTypeComboBox.setSelectedIndex(0);
        journeyDateField.setText("");
        fromField.setText("");
        toField.setText("");
    }

    private static boolean ValidDate(String date) {
        Matcher matcher = DATE_PATTERN.matcher(date);
        return matcher.matches();
    }

    private static void cancelReservation() {
        String pnrString = JOptionPane.showInputDialog(mainFrame, "Enter PNR number to cancel:");

        if (pnrString != null && !pnrString.isEmpty()) {
            try {
                int pnrNumber = Integer.parseInt(pnrString);

                for (Reservation reservation : reservations) {
                    if (reservation.getPnr() == pnrNumber) {
                        showReservationDetails(reservation);
                        int option = JOptionPane.showConfirmDialog(mainFrame, "Do you want to cancel this reservation?", "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            reservations.remove(reservation);
                            resultArea.append("Reservation canceled successfully. PNR: " + reservation.getPnr() + "\n");
                        }
                        return;
                    }
                }

                JOptionPane.showMessageDialog(mainFrame, "No reservation found with the provided PNR number.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid PNR format. Please enter a valid number.");
            }
        }
    }

    private static void showReservationDetails(Reservation reservation) {
        JOptionPane.showMessageDialog(mainFrame,
                "PNR: " + reservation.getPnr() + "\n" +
                        "Passenger Name: " + reservation.getPassengerName() + "\n" +
                        "Train Number: " + reservation.getTrainNumber() + "\n" +
                        "Train Name: " + reservation.getTrainName() + "\n" +
                        "Class Type: " + reservation.getClassType() + "\n" +
                        "Journey Date: " + reservation.getJourneyDate() + "\n" +
                        "From: " + reservation.getFrom() + "\n" +
                        "To: " + reservation.getTo());
    }

    private static class Reservation {
        private static int nextPnr = MIN_PNR;
        private int pnr;
        private String passengerName;
        private String trainNumber;
        private String trainName;
        private String classType;
        private String journeyDate;
        private String from;
        private String to;

        public Reservation(String passengerName, String trainNumber, String trainName, String classType, String journeyDate, String from, String to) {
            this.pnr = generatePnrNumber();
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

        private static int generatePnrNumber() {
            return nextPnr++;
        }
    }
}
