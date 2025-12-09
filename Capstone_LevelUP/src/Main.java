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
    private static Map<String, String> users = new HashMap<>();

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

    // Sample data
    private static final List<DailyChallenge> availableChallenges = Arrays.asList(
            new DailyChallenge("DC1", "Burn 500 Calories", "Burn 500 calories in workouts today", 500, DailyChallenge.TargetType.CALORIES),
            new DailyChallenge("DC2", "30 Minute Run", "Complete a 30-minute running session", 30, DailyChallenge.TargetType.RUN_DURATION),
            new DailyChallenge("DC3", "100 Pushups", "Complete 100 pushups today", 100, DailyChallenge.TargetType.PUSHUPS_REPS),
            new DailyChallenge("DC4", "200 Crunches", "Complete 200 crunches today", 200, DailyChallenge.TargetType.CRUNCHES_REPS),
            new DailyChallenge("DC5", "150 Squats", "Complete 150 squats today", 150, DailyChallenge.TargetType.SQUATS_REPS)
    );

    private static final List<Quest> availableQuests = Arrays.asList(
            new Quest("First Steps", 200, "WorkoutCount", 5),
            new Quest("Healthy Beginner", 300, "HealthyMeals", 10),
            new Quest("Streak Master", 500, "Streak", 7),
            new Quest("Level Up!", 1000, "LevelUp", 5),
            new Quest("Fitness Enthusiast", 800, "WorkoutCount", 20),
            new Quest("Nutrition Expert", 600, "HealthyMeals", 25)
    );

    public Main() {
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("LevelUP Fitness Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create different screens - only create login and register panels initially
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        // Create empty panels for other screens - they'll be populated when user logs in
        mainPanel.add(createEmptyPanel("DASHBOARD"), "DASHBOARD");
        mainPanel.add(createEmptyPanel("WORKOUT"), "WORKOUT");
        mainPanel.add(createEmptyPanel("MEAL"), "MEAL");
        mainPanel.add(createEmptyPanel("QUESTS"), "QUESTS");
        mainPanel.add(createEmptyPanel("CHALLENGES"), "CHALLENGES");

        add(mainPanel);
        showLoginScreen();
    }

    private JPanel createEmptyPanel(String panelName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        JLabel label = new JLabel(panelName + " - Please login first", JLabel.CENTER);
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

        // Title
        JLabel titleLabel = new JLabel("LevelUP Fitness", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        panel.add(loginUsernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        panel.add(loginPasswordField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton, gbc);

        // Register Link
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

        // Title
        JLabel titleLabel = new JLabel("Create Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        registerNameField = new JTextField(15);
        panel.add(registerNameField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerUsernameField = new JTextField(15);
        panel.add(registerUsernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerPasswordField = new JPasswordField(15);
        panel.add(registerPasswordField, gbc);

        // Register Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> handleRegistration());
        panel.add(registerButton, gbc);

        // Back to Login
        gbc.gridy = 5;
        JButton backButton = new JButton("Back to Login");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(new Color(0, 102, 204));
        backButton.addActionListener(e -> showLoginScreen());
        panel.add(backButton, gbc);

        return panel;
    }

    private void createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Navigation Menu
        JPanel navPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        navPanel.setBackground(new Color(240, 248, 255));

        String[] menuItems = {
                "View Stats", "Log Workout", "Log Meal",
                "View Quests", "Challenges", "Workout History"
        };

        Color[] colors = {
                new Color(0, 153, 76), new Color(204, 0, 0), new Color(255, 153, 0),
                new Color(102, 0, 204), new Color(0, 102, 204), new Color(153, 0, 153)
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuItems[i]);
            menuButton.setBackground(colors[i]);
            menuButton.setForeground(Color.WHITE);
            menuButton.setFont(new Font("Arial", Font.BOLD, 14));
            menuButton.addActionListener(new MenuButtonListener(menuItems[i]));
            navPanel.add(menuButton);
        }

        // Stats Area
        statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane statsScrollPane = new JScrollPane(statsTextArea);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(navPanel, BorderLayout.CENTER);
        panel.add(statsScrollPane, BorderLayout.SOUTH);

        // Replace the empty dashboard panel with the real one
        mainPanel.add(panel, "DASHBOARD");
    }

    private void createWorkoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(204, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Log Workout");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());
        headerPanel.add(backButton, BorderLayout.EAST);

        // Workout Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(240, 248, 255));

        JComboBox<String> workoutTypeCombo = new JComboBox<>(new String[]{"Cardio", "Strength"});
        JTextField nameField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField repsField = new JTextField();
        JTextField setsField = new JTextField();
        JComboBox<String> intensityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        formPanel.add(new JLabel("Workout Type:"));
        formPanel.add(workoutTypeCombo);
        formPanel.add(new JLabel("Workout Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Duration (mins):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Reps (Strength only):"));
        formPanel.add(repsField);
        formPanel.add(new JLabel("Sets (Strength only):"));
        formPanel.add(setsField);
        formPanel.add(new JLabel("Intensity:"));
        formPanel.add(intensityCombo);

        JButton logButton = new JButton("Log Workout");
        logButton.setBackground(new Color(0, 153, 76));
        logButton.setForeground(Color.WHITE);
        logButton.addActionListener(e -> {
            String type = (String) workoutTypeCombo.getSelectedItem();
            String name = nameField.getText();
            String intensity = (String) intensityCombo.getSelectedItem();

            try {
                Workout workout;
                if ("Cardio".equals(type)) {
                    int duration = Integer.parseInt(durationField.getText());
                    workout = new Workout(name, duration, intensity);
                } else {
                    int reps = Integer.parseInt(repsField.getText());
                    int sets = Integer.parseInt(setsField.getText());
                    workout = new Workout(name, reps, sets, intensity);
                }

                int xpGained = currentUser.logWorkout(workout);
                JOptionPane.showMessageDialog(this,
                        "Workout logged successfully!\n" +
                                "Calories burned: " + workout.getCaloriesBurned() + "\n" +
                                "XP gained: " + xpGained,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                nameField.setText("");
                durationField.setText("");
                repsField.setText("");
                setsField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for duration/reps/sets",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(logButton, BorderLayout.SOUTH);

        // Replace the empty workout panel with the real one
        mainPanel.add(panel, "WORKOUT");
    }

    private void createMealPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 153, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Log Meal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());
        headerPanel.add(backButton, BorderLayout.EAST);

        // Meal Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(240, 248, 255));

        JTextField nameField = new JTextField();
        JTextField caloriesField = new JTextField();
        JComboBox<String> mealTypeCombo = new JComboBox<>(
                new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});

        formPanel.add(new JLabel("Meal Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Calories:"));
        formPanel.add(caloriesField);
        formPanel.add(new JLabel("Meal Type:"));
        formPanel.add(mealTypeCombo);
        formPanel.add(new JLabel("")); // Empty cell
        formPanel.add(new JLabel("")); // Empty cell

        JButton logButton = new JButton("Log Meal");
        logButton.setBackground(new Color(0, 153, 76));
        logButton.setForeground(Color.WHITE);
        logButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int calories = Integer.parseInt(caloriesField.getText());
                String mealType = (String) mealTypeCombo.getSelectedItem();

                Meal meal = new Meal(name, calories, mealType);
                int xpGained = currentUser.logMeal(meal);

                String message = "Meal logged successfully!\n" +
                        "Healthy: " + (meal.isHealthy() ? "Yes" : "No");
                if (meal.isHealthy()) {
                    message += "\nXP gained: " + xpGained;
                }

                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                nameField.setText("");
                caloriesField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid calories number",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(logButton, BorderLayout.SOUTH);

        // Replace the empty meal panel with the real one
        mainPanel.add(panel, "MEAL");
    }

    private void createQuestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(102, 0, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Active Quests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());
        headerPanel.add(backButton, BorderLayout.EAST);

        JButton claimButton = new JButton("Claim Completed Quests");
        claimButton.addActionListener(e -> {
            currentUser.checkAndClaimQuests();
            updateQuestsPanel(panel);
        });
        headerPanel.add(claimButton, BorderLayout.CENTER);

        // Quests List
        updateQuestsPanel(panel);

        // Replace the empty quests panel with the real one
        mainPanel.add(panel, "QUESTS");
    }

    private void updateQuestsPanel(JPanel panel) {
        // Remove existing content except header
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                panel.remove(comp);
            }
        }

        JPanel questsPanel = new JPanel();
        questsPanel.setLayout(new BoxLayout(questsPanel, BoxLayout.Y_AXIS));
        questsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        questsPanel.setBackground(new Color(240, 248, 255));

        if (currentUser == null) {
            JLabel noUserLabel = new JLabel("Please login to view quests.");
            noUserLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questsPanel.add(noUserLabel);
        } else {
            List<Quest> activeQuests = currentUser.getActiveQuests();

            if (activeQuests.isEmpty()) {
                JLabel noQuestsLabel = new JLabel("No active quests available.");
                noQuestsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                questsPanel.add(noQuestsLabel);
            } else {
                for (Quest quest : activeQuests) {
                    JPanel questPanel = new JPanel(new BorderLayout());
                    questPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    questPanel.setBackground(quest.isClaimed() ? Color.LIGHT_GRAY : Color.WHITE);

                    JLabel nameLabel = new JLabel(quest.getQuestName());
                    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

                    JTextArea detailsArea = new JTextArea(
                            "Target: " + quest.getTargetAmount() + " " + quest.getQuestType() + "\n" +
                                    "Reward: " + quest.getRewardXP() + " XP\n" +
                                    "Status: " + (quest.isClaimed() ? "Claimed" :
                                    (quest.checkCompletion(currentUser) ? "Ready to Claim" : "In Progress")) + "\n" +
                                    "Time Left: " + quest.getTimeRemaining()
                    );
                    detailsArea.setEditable(false);
                    detailsArea.setBackground(quest.isClaimed() ? Color.LIGHT_GRAY : Color.WHITE);

                    questPanel.add(nameLabel, BorderLayout.NORTH);
                    questPanel.add(detailsArea, BorderLayout.CENTER);

                    questsPanel.add(questPanel);
                    questsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(questsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void createChallengesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Daily Challenge");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());
        headerPanel.add(backButton, BorderLayout.EAST);

        // Challenge Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(240, 248, 255));

        if (currentUser == null) {
            contentPanel.add(new JLabel("Please login to view daily challenge."), BorderLayout.CENTER);
        } else {
            DailyChallenge challenge = currentUser.getDailyChallenge();

            if (challenge == null) {
                contentPanel.add(new JLabel("No active daily challenge."), BorderLayout.CENTER);
            } else {
                JPanel challengePanel = new JPanel(new GridLayout(0, 1, 10, 10));
                challengePanel.setBackground(new Color(240, 248, 255));

                JLabel nameLabel = new JLabel(challenge.getChallengeName());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

                JTextArea descArea = new JTextArea(challenge.getDescription());
                descArea.setEditable(false);
                descArea.setBackground(new Color(240, 248, 255));

                JProgressBar progressBar = new JProgressBar(0, (int) challenge.getTargetValue());
                progressBar.setValue((int) challenge.getCurrentProgress());
                progressBar.setStringPainted(true);
                progressBar.setString(challenge.getProgressString());

                JLabel statusLabel = new JLabel("Status: " +
                        (challenge.isCompleted() ? "COMPLETED! 🎉" : "In Progress"));
                statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
                statusLabel.setForeground(challenge.isCompleted() ? new Color(0, 153, 76) : Color.BLACK);

                JLabel rewardLabel = new JLabel("Reward: 500 XP");

                challengePanel.add(nameLabel);
                challengePanel.add(descArea);
                challengePanel.add(progressBar);
                challengePanel.add(statusLabel);
                challengePanel.add(rewardLabel);

                contentPanel.add(challengePanel, BorderLayout.CENTER);
            }
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        // Replace the empty challenges panel with the real one
        mainPanel.add(panel, "CHALLENGES");
    }

    // Navigation methods
    private void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
        if (loginUsernameField != null) loginUsernameField.setText("");
        if (loginPasswordField != null) loginPasswordField.setText("");
    }

    private void showRegisterScreen() {
        cardLayout.show(mainPanel, "REGISTER");
        if (registerNameField != null) registerNameField.setText("");
        if (registerUsernameField != null) registerUsernameField.setText("");
        if (registerPasswordField != null) registerPasswordField.setText("");
    }

    private void showDashboard() {
        // Make sure all panels are created before showing dashboard
        if (currentUser != null) {
            createDashboardPanel();
            createWorkoutPanel();
            createMealPanel();
            createQuestsPanel();
            createChallengesPanel();
        }
        cardLayout.show(mainPanel, "DASHBOARD");
        updateDashboard();
    }

    private void updateDashboard() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");

            StringBuilder stats = new StringBuilder();
            stats.append("=== USER STATISTICS ===\n\n");
            stats.append("Level: ").append(currentUser.getLevel()).append("\n");
            stats.append("XP: ").append(currentUser.getXp()).append("/").append(currentUser.getLevel() * 500).append("\n");
            stats.append("Total Workouts: ").append(currentUser.getTotalWorkouts()).append("\n");
            stats.append("Healthy Meals: ").append(currentUser.getTotalHealthyMeals()).append("\n");
            stats.append("Current Streak: ").append(currentUser.getCurrentStreak().getCurrentStreak()).append(" days\n");
            stats.append("Streak Multiplier: ").append(currentUser.getCurrentStreak().getMultiplier()).append("x\n");
            stats.append("Active Quests: ").append(currentUser.getActiveQuests().size()).append("\n");
            stats.append("Earned Rewards: ").append(currentUser.getEarnedRewards().size()).append("\n");

            if (currentUser.getDailyChallenge() != null) {
                DailyChallenge dc = currentUser.getDailyChallenge();
                stats.append("\nDaily Challenge: ").append(dc.getChallengeName()).append("\n");
                stats.append("Progress: ").append(dc.getProgressString()).append("\n");
                stats.append("Status: ").append(dc.isCompleted() ? "COMPLETED" : "In Progress").append("\n");
            }

            statsTextArea.setText(stats.toString());
        }
    }

    // Event Handlers
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (users.containsKey(username)) {
            String hashed = users.get(username);
            if (verifyPassword(password, hashed)) {
                currentUser = User.authenticate(username, hashed);
                if (currentUser != null) {
                    initializeUserContent();
                    showDashboard();
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    private void handleRegistration() {
        String name = registerNameField.getText();
        String username = registerUsernameField.getText();
        String password = new String(registerPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(password);
        User newUser = User.register(name, username, hashedPassword);

        if (newUser != null) {
            users.put(username, hashedPassword);
            saveUserToFile(newUser);
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showLoginScreen();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogout() {
        currentUser = null;
        showLoginScreen();
    }

    private void initializeUserContent() {
        if (currentUser.getActiveQuests().isEmpty()) {
            currentUser.addQuest(availableQuests.get(0));
            currentUser.addQuest(availableQuests.get(1));
        }

        if (currentUser.getDailyChallenge() == null) {
            DailyChallenge randomChallenge = availableChallenges.get(
                    new Random().nextInt(availableChallenges.size())
            );
            currentUser.setDailyChallenge(randomChallenge);
        }
    }

    // Menu Button Listener
    private class MenuButtonListener implements ActionListener {
        private String menuItem;

        public MenuButtonListener(String menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (menuItem) {
                case "View Stats":
                    updateDashboard();
                    break;
                case "Log Workout":
                    cardLayout.show(mainPanel, "WORKOUT");
                    break;
                case "Log Meal":
                    cardLayout.show(mainPanel, "MEAL");
                    break;
                case "View Quests":
                    cardLayout.show(mainPanel, "QUESTS");
                    break;
                case "Challenges":
                    cardLayout.show(mainPanel, "CHALLENGES");
                    break;
                case "Workout History":
                    showWorkoutHistory();
                    break;
            }
        }
    }

    private void showWorkoutHistory() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login to view workout history.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Workout> workouts = currentUser.getWorkoutLog();

        StringBuilder history = new StringBuilder();
        history.append("=== WORKOUT HISTORY ===\n\n");
        history.append("Total workouts: ").append(workouts.size()).append("\n\n");

        if (workouts.isEmpty()) {
            history.append("No workouts logged yet.");
        } else {
            for (int i = 0; i < Math.min(workouts.size(), 10); i++) {
                Workout workout = workouts.get(i);
                history.append("• ").append(workout.getWorkoutName())
                        .append(" | ").append(workout.getType())
                        .append(" | ").append(String.format("%.0f", workout.getCaloriesBurned())).append(" cal")
                        .append(" | ").append(workout.getDurationMins()).append(" mins\n");
            }
        }

        JTextArea textArea = new JTextArea(history.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Workout History", JOptionPane.INFORMATION_MESSAGE);
    }

    // Authentication methods (unchanged)
    private static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes());

            byte[] combined = new byte[salt.length + hashedBytes.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedBytes, 0, combined, salt.length, hashedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);
            byte[] salt = new byte[16];
            byte[] storedHashBytes = new byte[combined.length - 16];

            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, storedHashBytes, 0, storedHashBytes.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes());

            return MessageDigest.isEqual(testHash, storedHashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (FileNotFoundException e) {
            // File not found means no users yet
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
        }
    }

    private static void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getUsername() + "," + user.getPassword());
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}