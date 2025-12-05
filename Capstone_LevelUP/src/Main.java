import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.io.*;

public class Main extends JFrame {
    private static User currentUser = null;
    private static final Map<String, User> users = new HashMap<>();
    private static final String USERS_DATA_FILE = "users_data.dat";

    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Login Panel
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // Register Panel
    private JTextField registerNameField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;

    // Dashboard Panel
    private JLabel welcomeLabel;
    private JTextArea statsTextArea;

    // Workout Panel specific
    private JComboBox<String> workoutTypeCombo;

    private void createTestUsers() {
        System.out.println(">> FORCE UPDATING Test Accounts...");
        String commonPass = hashPassword("123");

        // Level 1 User (Standard)
        User u1 = new User("T1", "Novice Tester", "test_lvl1", commonPass);
        users.put(u1.getUsername(), u1);

        // Level 5 User (Intermediate)
        User u5 = new User("T5", "Gym Rat", "test_lvl5", commonPass);
        for(int i=0; i<4; i++) {
            // Give enough XP to pass the current level threshold
            u5.gainXP(u5.getLevel() * 500 + 1);
        }
        u5.updateDailyChallenge(); // Refresh challenge for new level
        users.put(u5.getUsername(), u5);

        // Level 10 User (Advanced)
        User u10 = new User("T10", "Giga Chad", "test_lvl10", commonPass);
        for(int i=0; i<9; i++) {
            // Give enough XP to pass the current level threshold
            u10.gainXP(u10.getLevel() * 500 + 1);
        }
        u10.updateDailyChallenge(); // Refresh challenge for new level
        users.put(u10.getUsername(), u10);

        saveAllUsers();
    }

    public Main() {
        initializeUI();
        loadAllUsers();
        createTestUsers(); // <--- ADD THIS LINE HERE
    }

    private void initializeUI() {
        setTitle("LevelUP Fitness Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                saveAllUsers();
                System.out.println(">> Application closing - all user data saved.");
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createEmptyPanel("DASHBOARD"), "DASHBOARD");
        mainPanel.add(createEmptyPanel("WORKOUT"), "WORKOUT");
        mainPanel.add(createEmptyPanel("MEAL"), "MEAL");
        mainPanel.add(createEmptyPanel("CHALLENGES"), "CHALLENGES");

        add(mainPanel);
        showLoginScreen();
    }

    private JPanel createEmptyPanel(String panelName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        JLabel label = new JLabel(panelName + " - Please log in first", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LevelUP Fitness", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        panel.add(loginUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        panel.add(loginPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Log In");
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton registerLink = new JButton("Don't have an account? Register here");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(new Color(0, 102, 204));
        registerLink.addActionListener(e -> showRegisterScreen());
        panel.add(registerLink, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        registerNameField = new JTextField(15);
        panel.add(registerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerUsernameField = new JTextField(15);
        panel.add(registerUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerPasswordField = new JPasswordField(15);
        panel.add(registerPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> handleRegistration());
        panel.add(registerButton, gbc);

        gbc.gridy = 5;
        JButton backButton = new JButton("Back to Log In");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(new Color(0, 102, 204));
        backButton.addActionListener(e -> showLoginScreen());
        panel.add(backButton, gbc);

        return panel;
    }

    private JPanel createHeaderPanel(String title, Color backgroundColor, Runnable backAction) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> backAction.run());
        headerPanel.add(backButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = createHeaderPanel("Dashboard", new Color(0, 102, 204), this::showDashboard);
        welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        headerPanel.remove(1);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        JPanel navPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Adjusted grid
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        navPanel.setBackground(new Color(240, 248, 255));

        // Removed "View Quests" from menu
        String[] menuItems = {
                "View Stats", "Log Workout",
                "Log Meal", "Challenges", "Workout History"
        };

        Color[] colors = {
                new Color(0, 153, 76), new Color(204, 0, 0),
                new Color(255, 153, 0), new Color(0, 102, 204), new Color(153, 0, 153)
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuItems[i]);
            menuButton.setBackground(colors[i]);
            menuButton.setForeground(Color.WHITE);
            menuButton.setFont(new Font("Arial", Font.BOLD, 14));
            menuButton.addActionListener(new MenuButtonListener(menuItems[i]));
            navPanel.add(menuButton);
        }

        statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane statsScrollPane = new JScrollPane(statsTextArea);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(navPanel, BorderLayout.CENTER);
        panel.add(statsScrollPane, BorderLayout.SOUTH);

        mainPanel.add(panel, "DASHBOARD");
    }

    private void createWorkoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = createHeaderPanel("Log Workout", new Color(204, 0, 0), this::showDashboard);

        // UPDATED: Changed grid rows from 6 to 7 to accommodate the new "Category" dropdown
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(240, 248, 255));

        // 1. NEW: Category Dropdown (The Logic Controller)
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Select Type...", "Cardio", "Strength"});

        // Existing Exercise Name Dropdown
        workoutTypeCombo = new JComboBox<>();
        populateWorkoutCombo();

        // Input Fields
        JTextField durationField = new JTextField();
        JTextField repsField = new JTextField();
        JTextField setsField = new JTextField();
        JComboBox<String> intensityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        // 2. NEW: Default State - Disable everything initially
        disableField(durationField);
        disableField(repsField);
        disableField(setsField);

        // 3. NEW: The Logic Listener
        categoryCombo.addActionListener(e -> {
            String selected = (String) categoryCombo.getSelectedItem();
            if ("Cardio".equals(selected)) {
                enableField(durationField);
                disableField(repsField);
                disableField(setsField);
            } else if ("Strength".equals(selected)) {
                disableField(durationField);
                enableField(repsField);
                enableField(setsField);
            } else {
                disableField(durationField);
                disableField(repsField);
                disableField(setsField);
            }
        });

        // Add components to Grid (Order matters!)
        formPanel.add(new JLabel("Category:")); // New Row
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("Select Exercise:"));
        formPanel.add(workoutTypeCombo);

        formPanel.add(new JLabel("Duration (mins):"));
        formPanel.add(durationField);

        formPanel.add(new JLabel("Reps:"));
        formPanel.add(repsField);

        formPanel.add(new JLabel("Sets:"));
        formPanel.add(setsField);

        formPanel.add(new JLabel("Intensity:"));
        formPanel.add(intensityCombo);

        JButton logButton = new JButton("Log Workout");
        logButton.setBackground(new Color(0, 153, 76));
        logButton.setForeground(Color.WHITE);

        logButton.addActionListener(e -> {
            String selectedExercise = (String) workoutTypeCombo.getSelectedItem();
            String intensity = (String) intensityCombo.getSelectedItem();
            String category = (String) categoryCombo.getSelectedItem();

            // Basic validation
            if ("Select Type...".equals(category)) {
                JOptionPane.showMessageDialog(this, "Please select a Category (Cardio/Strength).");
                return;
            }

            try {
                int duration = 0, reps = 0, sets = 0;

                // Only parse fields if they are explicitly enabled
                if (durationField.isEnabled() && !durationField.getText().isEmpty())
                    duration = Integer.parseInt(durationField.getText());

                if (repsField.isEnabled() && !repsField.getText().isEmpty())
                    reps = Integer.parseInt(repsField.getText());

                if (setsField.isEnabled() && !setsField.getText().isEmpty())
                    sets = Integer.parseInt(setsField.getText());

                Workout workout;
                // Logic: If duration is set (Cardio), use that constructor.
                // Otherwise use Reps/Sets (Strength).
                if (duration > 0) {
                    workout = new Workout(selectedExercise, duration, intensity);
                } else {
                    workout = new Workout(selectedExercise, reps, sets, intensity);
                }

                int xpGained = currentUser.logWorkout(workout);
                autoSave();

                JOptionPane.showMessageDialog(this,
                        "Workout logged successfully!\n" +
                                "Calories burned: " + workout.getCaloriesBurned() + "\n" +
                                "XP gained: " + xpGained,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Reset fields
                categoryCombo.setSelectedIndex(0); // This triggers the listener to disable everything again
                durationField.setText("");
                repsField.setText("");
                setsField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(logButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "WORKOUT");
    }

    // --- UI Helper Methods ---

    private void enableField(JTextField field) {
        field.setEnabled(true);
        field.setBackground(Color.WHITE);
    }

    private void disableField(JTextField field) {
        field.setEnabled(false);
        field.setText(""); // Clears data to prevent accidental submission
        field.setBackground(new Color(229, 229, 229)); // Light grey visual cue
    }

    private void populateWorkoutCombo() {
        if (currentUser == null) return;
        workoutTypeCombo.removeAllItems();

        DailyChallenge dc = currentUser.getDailyChallenge();
        if (dc != null && dc.getTasks() != null) {
            for (ChallengeTask task : dc.getTasks()) {
                // IMPORTANT: Filter out "Calories" or non-exercise tasks if you wish
                if (!task.getTaskName().equalsIgnoreCase("Calories") &&
                        !task.getTaskName().equalsIgnoreCase("Steps")) {
                    workoutTypeCombo.addItem(task.getTaskName());
                }
            }
        }
        // Fallback option in case list is empty or logic fails
        workoutTypeCombo.addItem("Other Workout");
    }

    private void createMealPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = createHeaderPanel("Log Meal", new Color(255, 153, 0), this::showDashboard);

        JPanel mainContainer = new JPanel(new CardLayout());
        mainContainer.setBackground(new Color(240, 248, 255));

        // Simplified Meal Panel for now (restoring complex one makes file huge)
        // You can paste your complex meal panel logic back here if you have it saved
        JPanel simpleForm = new JPanel(new GridLayout(3, 2));
        JTextField mealNameField = new JTextField();
        JTextField caloriesField = new JTextField();
        JButton logBtn = new JButton("Log Meal");

        simpleForm.add(new JLabel("Meal Name:")); simpleForm.add(mealNameField);
        simpleForm.add(new JLabel("Calories:")); simpleForm.add(caloriesField);
        simpleForm.add(new JLabel("")); simpleForm.add(logBtn);

        logBtn.addActionListener(e -> {
            try {
                int cal = Integer.parseInt(caloriesField.getText());
                Meal m = new Meal(mealNameField.getText(), cal, "Snack");
                currentUser.logMeal(m);
                JOptionPane.showMessageDialog(this, "Meal Logged!");
                autoSave();
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(simpleForm, BorderLayout.CENTER);
        mainPanel.add(panel, "MEAL");
    }

    private void createChallengesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = createHeaderPanel("Daily Challenge", new Color(0, 102, 204), this::showDashboard);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 248, 255));

        if (currentUser != null && currentUser.getDailyChallenge() != null) {
            DailyChallenge challenge = currentUser.getDailyChallenge();

            StringBuilder tasksStr = new StringBuilder();
            if (challenge.getTasks() != null) {
                for (ChallengeTask task : challenge.getTasks()) {
                    String check = task.isComplete() ? "[x] " : "[ ] ";
                    tasksStr.append(check).append(task.getTaskName())
                            .append(": ").append(task.getProgressDisplay()).append("\n");
                }
            }

            JTextArea challengeInfo = new JTextArea(
                    "Challenge: " + challenge.getChallengeName() + "\n\n" +
                            "Description: " + challenge.getDescription() + "\n\n" +
                            "Tasks:\n" + tasksStr.toString() + "\n" +
                            "Time Left: " + challenge.displayTimer()
            );
            challengeInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));
            challengeInfo.setEditable(false);
            contentPanel.add(new JScrollPane(challengeInfo), BorderLayout.CENTER);
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(panel, "CHALLENGES");
    }

    private static void loadAllUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_DATA_FILE))) {
            Map<String, User> loaded = (Map<String, User>) ois.readObject();
            users.clear();
            users.putAll(loaded);
        } catch (Exception e) {}
    }

    private static void saveAllUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {}
    }

    private void autoSave() {
        if (currentUser != null) {
            users.put(currentUser.getUsername(), currentUser);
            saveAllUsers();
        }
    }

    private void showLoginScreen() { cardLayout.show(mainPanel, "LOGIN"); loginPasswordField.setText(""); }
    private void showRegisterScreen() { cardLayout.show(mainPanel, "REGISTER"); registerPasswordField.setText(""); }
    private void showDashboard() {
        if (currentUser != null) {
            createDashboardPanel();
            createWorkoutPanel(); // Recreates it to refresh the dropdown
            createMealPanel();
            createChallengesPanel();
        }
        cardLayout.show(mainPanel, "DASHBOARD");
        updateDashboard();
    }

    private void updateDashboard() {
        if (currentUser != null) {
            currentUser.updateDailyChallenge();

            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            StringBuilder stats = new StringBuilder();
            stats.append("Level: ").append(currentUser.getLevel()).append("\n");
            stats.append("XP: ").append(currentUser.getXp()).append("\n");
            stats.append("Streak: ").append(currentUser.getCurrentStreak().getCurrentStreak()).append(" Days\n");
            stats.append("Weekly Mins: ").append(currentUser.getTotalWeeklyMinutes());
            statsTextArea.setText(stats.toString());
        }
    }

    private void handleLogin() {
        String u = loginUsernameField.getText();
        String p = new String(loginPasswordField.getPassword());
        if (users.containsKey(u) && verifyPassword(p, users.get(u).getPassword())) {
            currentUser = users.get(u);
            showDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Login");
        }
    }

    private void handleRegistration() {
        String u = registerUsernameField.getText();
        String p = new String(registerPasswordField.getPassword());
        if (users.containsKey(u)) {
            JOptionPane.showMessageDialog(this, "Username Taken");
            return;
        }
        User newUser = User.register(registerNameField.getText(), u, hashPassword(p));
        if (newUser != null) {
            users.put(u, newUser);
            autoSave();
            JOptionPane.showMessageDialog(this, "Registered!");
            showLoginScreen();
        }
    }

    private void handleLogout() {
        autoSave();
        currentUser = null;
        showLoginScreen();
    }

    private void showWorkoutHistory() {
        if (currentUser == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("=== Workout History ===\n");
        for(Workout w : currentUser.getWorkoutLog()) {
            sb.append(w.getWorkoutName()).append(" | ").append(w.getType()).append(" | ").append(w.getDate()).append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Workout History", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String hashPassword(String password) { return Integer.toString(password.hashCode()); }
    private static boolean verifyPassword(String password, String storedHash) { return Integer.toString(password.hashCode()).equals(storedHash); }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Main().setVisible(true)); }

    private class MenuButtonListener implements ActionListener {
        private String item;
        public MenuButtonListener(String item) { this.item = item; }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (item) {
                case "View Stats": updateDashboard(); break;
                case "Log Workout": cardLayout.show(mainPanel, "WORKOUT"); break;
                case "Log Meal": cardLayout.show(mainPanel, "MEAL"); break;
                case "Challenges": cardLayout.show(mainPanel, "CHALLENGES"); break;
                case "Workout History": showWorkoutHistory(); break;
            }
        }
    }
}