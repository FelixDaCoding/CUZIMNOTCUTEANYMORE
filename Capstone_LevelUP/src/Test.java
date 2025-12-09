import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Test extends JFrame {
    // === UI COMPONENTS ===
    private JPanel LevelUP, Start, CreateAcc, SignIn, ForgotPass;
    private JPanel HomePage, StreakPage, ChallPage, LogPage, AccPage;
    private JPanel TimerJPanel; // Added back
    private JLabel AppFBAccPage, CheckLogAccPage; // Added back
    private JLabel ChallengeTitle;

    // Auth & Inputs
    private JButton SignInPageBT, CreateAccPageBT, CreateAccountBT, SignInBT, ChangePasswordButton, SignOutButton;
    private JTextField CreateUserTextField, CheckUsernameField, EnterUsernameTextField;
    private JTextField CreatePassTextField, CheckPassField; // Keep as JTextField for GUI Designer
    private JTextField EnterNewPasswordTextField; // Keep as JTextField
    private JLabel SignInForgotPassword, CPassAccPage;

    // Navigation Labels
    private JLabel STKBTNHomePage, CHALBTNHomePage, LOGBTNHomePage, ACCBTNHomePage;
    private JLabel HMBTNStreakPage, CHALBTNStreakPage, LOGBTNStreakPage, ACCBTNStreakPage;
    private JLabel HMBTNChallPage, STKBTNChallPage, LOGBTNChallPage, ACCBTNChallPage;
    private JLabel HMBTNLogPage, STKBTNLogPage, CHALBTNLogPage, ACCBTNLogPage;
    private JLabel HMBTNAccPage, STKBTNAccPage, CHALBTNAccPage, LOGBTNAccPage;

    // Data Display Labels
    private JLabel FIRESTREAK, USERNAME, ACCOUNTID;
    private JLabel HomePageDisplayName;
    private JLabel HomePageDisplayDate;
    private JProgressBar HMPageProgressBar;
    private JLabel HMPageStreakCount, HMPageEXPCount, StreakCount;

    // Calendar Day Labels
    private JPanel Days; // ADDED BACK - This was missing!
    private JLabel SDay, MDay, TDay, WDay, ThDay, FDay, StDay;

    // Challenge Page
    private JPanel Challenge; // ADDED BACK
    private JProgressBar ChallPageProgressBar;
    private JButton CompleteChallengeButton;
    private JLabel RewardLabel, RewardCount;
    private JCheckBox Chall1, Chall2, Chall3, Chall4;
    private JLabel ChallCount1, ChallCount2, ChallCount3, ChallCount4;

    // Log Page
    private JButton LogWorkoutButton, LogMealButton;
    private JComboBox<String> ProteinComboBox, CarbohydratesComboBox, VegetablesComboBox;

    // Checkboxes
    private JCheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8;
    private JCheckBox checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16;
    private JLabel GoBackCAcc;
    private JLabel GoBackSignIn;
    private JLabel GoBackForgPass;

    // === BACKEND DATA ===
    private static User currentUser = null;
    private static final Map<String, String> userCredentials = new HashMap<>();
    private java.util.List<JCheckBox> workoutCheckBoxes;
    private java.util.List<JCheckBox> challengeCheckBoxes;
    private final Map<String, Integer> calorieMap = new HashMap<>();

    Test() {
        // --- SETUP ---
        setContentPane(LevelUP);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setTitle("LevelUP Fitness");

        // --- INITIALIZATION ---
        loadCredentials();
        initializeLists();
        setupLogPageCheckboxes();
        setupLogPageComboBoxes();
        setupPlaceholders();
        setupNavigation();
        setupChallengeLogic();

        // Start at Start Page
        switchCard("Start");

        // --- BUTTON ACTIONS ---
        SignInPageBT.addActionListener(e -> switchCard("SignIn"));
        CreateAccPageBT.addActionListener(e -> switchCard("CreateAcc"));
        SignInBT.addActionListener(e -> handleLogin());
        CreateAccountBT.addActionListener(e -> handleRegistration());
        SignOutButton.addActionListener(e -> handleLogout());

        // --- CHANGE PASSWORD LOGIC ---
        ChangePasswordButton.addActionListener(e -> {
            String username = EnterUsernameTextField.getText();
            String newPass = EnterNewPasswordTextField.getText();

            if (username.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and new password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!userCredentials.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash the new password before storing
            String hashedNewPass = hashPassword(newPass);
            userCredentials.put(username, hashedNewPass);
            saveCredentials();

            JOptionPane.showMessageDialog(this, "Password Changed Successfully!");
            EnterUsernameTextField.setText("");
            EnterNewPasswordTextField.setText("");
            switchCard("SignIn");
        });

        // Forgot Password Links
        MouseAdapter forgotPassAdapter = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                switchCard("ForgotPass");
            }
        };

        if (SignInForgotPassword != null) {
            SignInForgotPassword.addMouseListener(forgotPassAdapter);
            SignInForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        if (CPassAccPage != null) {
            CPassAccPage.addMouseListener(forgotPassAdapter);
            CPassAccPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // --- COMPLETE CHALLENGE ---
        CompleteChallengeButton.addActionListener(e -> {
            if (currentUser == null) return;
            if (ChallPageProgressBar.getValue() == 100) {
                Workout challengeWorkout = new Workout("Daily Challenge", 0, "Low");
                currentUser.logWorkout(challengeWorkout);

                forceStreakUpdate();
                currentUser.gainXP(500);

                saveStreakHistoryDate(LocalDate.now());
                saveUserStreakData();
                saveDailyChallengeState(true);

                lockChallengeUI(true);

                JOptionPane.showMessageDialog(this, "Challenge Completed!\nXP Gained: 500\nStreak Updated!");
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(this, "Complete all tasks first!", "Incomplete", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 2. LOG WORKOUT
        LogWorkoutButton.addActionListener(e -> handleLogWorkout());

        // 3. LOG MEAL
        LogMealButton.addActionListener(e -> handleLogMeal());

        GoBackCAcc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) LevelUP.getLayout();
                cl.show(LevelUP, "Start");
                GoBackCAcc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        GoBackSignIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) LevelUP.getLayout();
                cl.show(LevelUP, "CreateAcc");
                GoBackSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        GoBackForgPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) LevelUP.getLayout();
                cl.show(LevelUP, "SignIn");
                GoBackForgPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });
    }

    // =========================================================
    // === PASSWORD SECURITY METHODS ===
    // =========================================================

    private String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    private boolean verifyPassword(String username, String inputPassword) {
        String storedHash = userCredentials.get(username);
        if (storedHash == null) return false;

        String inputHash = hashPassword(inputPassword);
        return storedHash.equals(inputHash);
    }

    // =========================================================
    // === AUTHENTICATION & USER LOADING ===
    // =========================================================

    private void handleLogin() {
        String username = CheckUsernameField.getText();
        String password = CheckPassField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!userCredentials.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "User not found. Please check your username or create an account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            CheckUsernameField.setText("");
            CheckPassField.setText("");
            CheckUsernameField.requestFocus();
            return;
        }

        if (!verifyPassword(username, password)) {
            JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            CheckPassField.setText("");
            CheckPassField.requestFocus();
            return;
        }

        currentUser = new User("ID-" + username.hashCode(), username, username, password);
        loadUserStreakData();

        JOptionPane.showMessageDialog(this, "Welcome, " + username + "!");

        CheckPassField.setText("");
        CheckUsernameField.setText("");

        refreshUI();
        switchCard("HomePage");
    }

    private void handleRegistration() {
        String username = CreateUserTextField.getText();
        String password = CreatePassTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userCredentials.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(password);
        userCredentials.put(username, hashedPassword);
        saveCredentials();

        User newUser = User.register(username, username, password);
        if (newUser != null) {
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
            CreateUserTextField.setText("");
            CreatePassTextField.setText("");
            switchCard("SignIn");
        }
    }

    private void handleLogout() {
        if (currentUser != null) {
            saveUserStreakData();
        }
        currentUser = null;

        CheckUsernameField.setText("");
        CheckPassField.setText("");
        CreateUserTextField.setText("");
        CreatePassTextField.setText("");
        EnterUsernameTextField.setText("");
        EnterNewPasswordTextField.setText("");

        switchCard("Start");
    }

    // =========================================================
    // === LOGGING LOGIC ===
    // =========================================================

    private void handleLogWorkout() {
        if (currentUser == null) return;

        StringBuilder activityName = new StringBuilder("Workout: ");
        int exerciseCount = 0;
        boolean hasSelection = false;

        if (workoutCheckBoxes != null) {
            for (JCheckBox cb : workoutCheckBoxes) {
                if (cb != null && cb.isSelected()) {
                    activityName.append(cb.getText()).append(", ");
                    hasSelection = true;
                    exerciseCount++;
                    cb.setSelected(false);
                }
            }
        }

        if (!hasSelection) {
            JOptionPane.showMessageDialog(this, "Select an exercise.");
            return;
        }

        int xpToAward = exerciseCount * 10;
        String logText = "Workouts: " + activityName.toString();
        writeToLogFile(logText);

        Workout w = new Workout("Session: " + exerciseCount + " Exercises", 0, "Low");
        currentUser.logWorkout(w);
        currentUser.gainXP(xpToAward);

        saveStreakHistoryDate(LocalDate.now());

        JOptionPane.showMessageDialog(this, "Logged: " + exerciseCount + " Exercises\nGained " + xpToAward + " XP.");
        updateChallengeStateFromLogs();
        refreshUI();
    }

    private void writeToLogFile(String text) {
        String filename = "Log_" + currentUser.getUsername() + "_Workouts.txt";
        String entry = "[" + LocalDate.now() + "] " + text;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogMeal() {
        if (currentUser == null) return;

        String protein = (String) ProteinComboBox.getSelectedItem();
        String carb = (String) CarbohydratesComboBox.getSelectedItem();
        String veg = (String) VegetablesComboBox.getSelectedItem();

        if (protein == null || carb == null || veg == null) return;

        int totalCals = calorieMap.getOrDefault(protein, 0)
                + calorieMap.getOrDefault(carb, 0)
                + calorieMap.getOrDefault(veg, 0);

        String mealName = "Combo: " + protein + " + " + carb;
        Meal m = new Meal(mealName, totalCals, "Lunch");

        int xp = currentUser.logMeal(m);

        String status = m.isHealthy() ? "Healthy Choice! (+50 XP)" : "High Calorie Meal (No XP)";
        JOptionPane.showMessageDialog(this,
                "Meal Logged!\n" +
                        "Total Calories: " + totalCals + "\n" +
                        "Verdict: " + status);

        ProteinComboBox.setSelectedIndex(0);
        CarbohydratesComboBox.setSelectedIndex(0);
        VegetablesComboBox.setSelectedIndex(0);

        refreshUI();
    }

    // =========================================================
    // === UI REFRESH ===
    // =========================================================

    private void refreshUI() {
        refreshHomePage();
        refreshStreakPage();
        refreshAccountPage();
        refreshChallengePage();
    }

    private void refreshHomePage() {
        if (currentUser == null) return;
        HomePageDisplayName.setText("Hi, " + currentUser.getUsername() + "!");

        if (HomePageDisplayDate != null) {
            HomePageDisplayDate.setText(LocalDate.now().toString());
        }

        if (currentUser.getCurrentStreak() != null) {
            HMPageStreakCount.setText(String.valueOf(currentUser.getCurrentStreak().getCurrentStreak()));
        }
        HMPageEXPCount.setText(currentUser.getXp() + " XP");

        int level = currentUser.getLevel();
        int xp = currentUser.getXp();
        int startXP = (level - 1) * 500;
        int progress = (int) (((double)(xp - startXP) / 500) * 100);
        HMPageProgressBar.setValue(Math.max(0, Math.min(100, progress)));
    }

    private void refreshStreakPage() {
        if (currentUser == null) return;

        int currentStreak = currentUser.getCurrentStreak().getCurrentStreak();
        StreakCount.setText(String.valueOf(currentStreak));

        if (currentStreak > 0) {
            StreakCount.setForeground(new Color(255, 204, 0));
        } else {
            StreakCount.setForeground(Color.BLACK);
        }

        String imageName = (currentStreak > 0) ? "ACT Streak.png" : "INC Streak.png";
        java.net.URL imgURL = getClass().getResource("Resources/" + imageName);
        if (imgURL == null) imgURL = getClass().getResource("/Resources/" + imageName);

        if (imgURL != null) {
            FIRESTREAK.setIcon(new ImageIcon(imgURL));
        } else {
            try {
                FIRESTREAK.setIcon(new ImageIcon("Resources/" + imageName));
            } catch (Exception e) {
                System.out.println("Could not find image: " + imageName);
            }
        }

        Set<String> activeDates = loadStreakHistoryDates();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        JLabel[] days = {SDay, MDay, TDay, WDay, ThDay, FDay, StDay};

        for (int i = 0; i < days.length; i++) {
            LocalDate checkDate = startOfWeek.plusDays(i);
            if (activeDates.contains(checkDate.toString())) {
                days[i].setForeground(new Color(255, 204, 0));
            } else {
                days[i].setForeground(Color.BLACK);
            }
        }
    }

    private void refreshAccountPage() {
        if (currentUser != null) {
            USERNAME.setText(currentUser.getUsername());
            ACCOUNTID.setText("ID: " + currentUser.getUserId());
        }
    }

    private void refreshChallengePage() {
        RewardLabel.setText("XP Bonus");
        RewardCount.setText("500 XP");
        updateChallengeStateFromLogs();
    }

    // =========================================================
    // === CHALLENGE LOGIC ===
    // =========================================================

    private void setupChallengeLogic() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String title = "Daily Challenge";
        String[][] exercises = new String[4][2];

        switch (today) {
            case MONDAY:
                title = "Monday: Push & Power";
                exercises[0] = new String[]{"Push-Ups", "3 | 12"};
                exercises[1] = new String[]{"Tricep Dips", "3 | 10"};
                exercises[2] = new String[]{"Shoulder Taps", "3 | 20"};
                exercises[3] = new String[]{"Plank", "3 | 45s"};
                break;
            case TUESDAY:
                title = "Tuesday: Leg Day";
                exercises[0] = new String[]{"Squats", "4 | 15"};
                exercises[1] = new String[]{"Lunges", "3 | 12"};
                exercises[2] = new String[]{"Calf Raises", "3 | 20"};
                exercises[3] = new String[]{"Wall Sit", "3 | 45s"};
                break;
            case WEDNESDAY:
                title = "Wednesday: Upper & Back";
                exercises[0] = new String[]{"Superman", "3 | 15"};
                exercises[1] = new String[]{"Push-Ups", "3 | 15"};
                exercises[2] = new String[]{"Shoulder Taps", "3 | 20"};
                exercises[3] = new String[]{"Tricep Dips", "3 | 12"};
                break;
            case THURSDAY:
                title = "Thursday: Cardio Burn";
                exercises[0] = new String[]{"Jumping Jacks", "3 | 50"};
                exercises[1] = new String[]{"High Knees", "3 | 30s"};
                exercises[2] = new String[]{"Mtn Climbers", "3 | 30s"};
                exercises[3] = new String[]{"Burpees", "3 | 10"};
                break;
            case FRIDAY:
                title = "Friday: Core Crusher";
                exercises[0] = new String[]{"Sit-Ups", "3 | 20"};
                exercises[1] = new String[]{"Russian Twists", "3 | 20"};
                exercises[2] = new String[]{"Leg Raises", "3 | 15"};
                exercises[3] = new String[]{"Plank", "3 | 60s"};
                break;
            case SATURDAY:
                title = "Saturday: Full Body HIIT";
                exercises[0] = new String[]{"Burpees", "3 | 12"};
                exercises[1] = new String[]{"Squats", "3 | 20"};
                exercises[2] = new String[]{"Push-Ups", "3 | 15"};
                exercises[3] = new String[]{"Jumping Jacks", "3 | 60"};
                break;
            case SUNDAY:
                title = "Sunday: Active Recovery";
                exercises[0] = new String[]{"Wall Sit", "3 | 60s"};
                exercises[1] = new String[]{"Superman", "3 | 15"};
                exercises[2] = new String[]{"Calf Raises", "3 | 20"};
                exercises[3] = new String[]{"Plank", "2 | 90s"};
                break;
        }

        if (ChallengeTitle != null) ChallengeTitle.setText(title);

        if (Chall1 != null) {
            Chall1.setText(exercises[0][0]);
            ChallCount1.setText(exercises[0][1]);
            Chall2.setText(exercises[1][0]);
            ChallCount2.setText(exercises[1][1]);
            Chall3.setText(exercises[2][0]);
            ChallCount3.setText(exercises[2][1]);
            Chall4.setText(exercises[3][0]);
            ChallCount4.setText(exercises[3][1]);

            Chall1.setEnabled(false);
            Chall2.setEnabled(false);
            Chall3.setEnabled(false);
            Chall4.setEnabled(false);
        }
    }

    private void updateChallengeStateFromLogs() {
        if (currentUser == null) return;

        boolean c1 = isWorkoutLoggedToday(Chall1.getText());
        boolean c2 = isWorkoutLoggedToday(Chall2.getText());
        boolean c3 = isWorkoutLoggedToday(Chall3.getText());
        boolean c4 = isWorkoutLoggedToday(Chall4.getText());

        if (Chall1 != null) Chall1.setSelected(c1);
        if (Chall2 != null) Chall2.setSelected(c2);
        if (Chall3 != null) Chall3.setSelected(c3);
        if (Chall4 != null) Chall4.setSelected(c4);

        int count = 0;
        if (c1) count++;
        if (c2) count++;
        if (c3) count++;
        if (c4) count++;

        if (ChallPageProgressBar != null) {
            ChallPageProgressBar.setValue(count * 25);
        }

        boolean alreadyClaimed = isChallengeClaimedToday();
        if (CompleteChallengeButton != null) {
            if (alreadyClaimed) {
                CompleteChallengeButton.setText("Completed Today");
                CompleteChallengeButton.setEnabled(false);
            } else if (count == 4) {
                CompleteChallengeButton.setText("Complete Challenge");
                CompleteChallengeButton.setEnabled(true);
            } else {
                CompleteChallengeButton.setText("Complete Challenge");
                CompleteChallengeButton.setEnabled(false);
            }
        }
    }

    private boolean isWorkoutLoggedToday(String activityName) {
        if (currentUser == null) return false;
        String filename = "Log_" + currentUser.getUsername() + "_Workouts.txt";
        File f = new File(filename);
        if (!f.exists()) return false;

        String todayTag = "[" + LocalDate.now().toString();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(todayTag)) {
                    if (line.toLowerCase().contains(activityName.toLowerCase()))
                        return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isChallengeClaimedToday() {
        File f = new File("Challenge_" + currentUser.getUsername() + "_" + LocalDate.now() + ".txt");
        return f.exists();
    }

    private void saveDailyChallengeState(boolean completed) {
        if (currentUser == null) return;
        try (FileWriter fw = new FileWriter("Challenge_" + currentUser.getUsername() + "_" + LocalDate.now() + ".txt")) {
            fw.write("COMPLETED=" + completed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lockChallengeUI(boolean lock) {
        if (CompleteChallengeButton != null) {
            CompleteChallengeButton.setEnabled(!lock);
            if (lock)
                CompleteChallengeButton.setText("Completed Today");
        }
    }

    // =========================================================
    // === PERSISTENCE HELPERS ===
    // =========================================================

    private void saveStreakHistoryDate(LocalDate date) {
        if (currentUser == null) return;
        String filename = "StreakHistory_" + currentUser.getUsername() + ".txt";
        Set<String> dates = loadStreakHistoryDates();
        if (dates.contains(date.toString())) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(date.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> loadStreakHistoryDates() {
        Set<String> dates = new HashSet<>();
        if (currentUser == null) return dates;
        File f = new File("StreakHistory_" + currentUser.getUsername() + ".txt");
        if (!f.exists()) return dates;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null)
                dates.add(line.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dates;
    }

    private void saveUserStreakData() {
        if (currentUser == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("UserStreak_" + currentUser.getUsername() + ".txt"))) {
            writer.write(String.valueOf(currentUser.getCurrentStreak().getCurrentStreak()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserStreakData() {
        if (currentUser == null) return;
        File f = new File("UserStreak_" + currentUser.getUsername() + ".txt");
        if (f.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                int streak = Integer.parseInt(reader.readLine());
                java.lang.reflect.Field field = Streak.class.getDeclaredField("currentStreak");
                field.setAccessible(true);
                field.setInt(currentUser.getCurrentStreak(), streak);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void forceStreakUpdate() {
        if (currentUser == null || currentUser.getCurrentStreak() == null) return;
        Streak streak = currentUser.getCurrentStreak();
        try {
            java.lang.reflect.Field dateField = Streak.class.getDeclaredField("lastActiveDate");
            dateField.setAccessible(true);
            Date yesterday = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
            dateField.set(streak, yesterday);
            streak.updateStreak(currentUser);
        } catch (Exception e) {
            System.out.println("Streak update error: " + e.getMessage());
        }
    }

    private void setupLogPageCheckboxes() {
        if (checkBox1 == null) return;

        checkBox1.setText("Push-Ups");
        checkBox2.setText("Tricep Dips");
        checkBox3.setText("Shoulder Taps");
        checkBox4.setText("Superman");

        checkBox5.setText("Sit-Ups");
        checkBox6.setText("Russian Twists");
        checkBox7.setText("Leg Raises");
        checkBox8.setText("Plank");

        checkBox9.setText("Squats");
        checkBox10.setText("Lunges");
        checkBox11.setText("Calf Raises");
        checkBox12.setText("Wall Sit");

        checkBox13.setText("Jumping Jacks");
        checkBox14.setText("High Knees");
        checkBox15.setText("Burpees");
        checkBox16.setText("Mtn Climbers");
    }

    private void setupLogPageComboBoxes() {
        if (ProteinComboBox == null || CarbohydratesComboBox == null || VegetablesComboBox == null) return;

        ProteinComboBox.removeAllItems();
        CarbohydratesComboBox.removeAllItems();
        VegetablesComboBox.removeAllItems();

        addItem(ProteinComboBox, "Choose Protein", 0);
        addItem(ProteinComboBox, "Chicken Breast (Grilled)", 165);
        addItem(ProteinComboBox, "Beef Steak (Lean)", 250);
        addItem(ProteinComboBox, "Grilled Salmon", 200);
        addItem(ProteinComboBox, "Tofu (Steamed)", 100);
        addItem(ProteinComboBox, "Boiled Eggs (2)", 155);
        addItem(ProteinComboBox, "Canned Tuna", 130);
        addItem(ProteinComboBox, "Pork Chop", 200);
        addItem(ProteinComboBox, "Lentils (1 Cup)", 230);

        addItem(CarbohydratesComboBox, "Choose Carbohydrates", 0);
        addItem(CarbohydratesComboBox, "White Rice (1 Cup)", 200);
        addItem(CarbohydratesComboBox, "Brown Rice (1 Cup)", 215);
        addItem(CarbohydratesComboBox, "Quinoa", 220);
        addItem(CarbohydratesComboBox, "Pasta", 200);
        addItem(CarbohydratesComboBox, "Sweet Potato", 100);
        addItem(CarbohydratesComboBox, "Oatmeal", 150);
        addItem(CarbohydratesComboBox, "Whole Wheat Bread", 160);
        addItem(CarbohydratesComboBox, "Mashed Potatoes", 200);

        addItem(VegetablesComboBox, "Choose Vegetable", 0);
        addItem(VegetablesComboBox, "Steamed Broccoli", 55);
        addItem(VegetablesComboBox, "Spinach", 20);
        addItem(VegetablesComboBox, "Carrots", 40);
        addItem(VegetablesComboBox, "Green Beans", 30);
        addItem(VegetablesComboBox, "Asparagus", 20);
        addItem(VegetablesComboBox, "Bell Peppers", 30);
        addItem(VegetablesComboBox, "Kale Salad", 35);
        addItem(VegetablesComboBox, "Mixed Green Salad", 25);
    }

    private void addItem(JComboBox<String> box, String name, int cals) {
        box.addItem(name);
        calorieMap.put(name, cals);
    }

    private void loadCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length > 1)
                    userCredentials.put(p[0], p[1]);
            }
        } catch (Exception e) {
            // File might not exist yet - that's OK
        }
    }

    private void saveCredentials() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (Map.Entry<String, String> e : userCredentials.entrySet()) {
                bw.write(e.getKey() + "," + e.getValue());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // === BOILERPLATE ===
    // =========================================================

    private void switchCard(String cardName) {
        CardLayout cl = (CardLayout) LevelUP.getLayout();
        cl.show(LevelUP, cardName);
    }

    private void setupNavigation() {
        bindNav(Arrays.asList(LOGBTNs(), LOGBTNHomePage, LOGBTNStreakPage, LOGBTNChallPage, LOGBTNAccPage), "LogPage", () -> {
            if (workoutCheckBoxes != null)
                for (JCheckBox c : workoutCheckBoxes)
                    c.setSelected(false);
        });

        bindNav(Arrays.asList(HMBTNs(), HMBTNStreakPage, HMBTNChallPage, HMBTNLogPage, HMBTNAccPage), "HomePage", this::refreshHomePage);
        bindNav(Arrays.asList(STKBTNs(), STKBTNHomePage, STKBTNChallPage, STKBTNLogPage, STKBTNAccPage), "StreakPage", this::refreshStreakPage);
        bindNav(Arrays.asList(CHALBTNs(), CHALBTNHomePage, CHALBTNStreakPage, CHALBTNLogPage, CHALBTNAccPage), "ChallPage", this::refreshChallengePage);
        bindNav(Arrays.asList(ACCBTNs(), ACCBTNHomePage, ACCBTNStreakPage, ACCBTNChallPage, ACCBTNLogPage), "AccPage", this::refreshAccountPage);
    }

    private void bindNav(java.util.List<JLabel> buttons, String card, Runnable action) {
        for (JLabel btn : buttons) {
            if (btn == null) continue;
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    switchCard(card);
                    if (action != null)
                        action.run();
                }
            });
        }
    }

    // Helper dummies for array lists
    private JLabel HMBTNs() { return null; }
    private JLabel STKBTNs() { return null; }
    private JLabel CHALBTNs() { return null; }
    private JLabel LOGBTNs() { return null; }
    private JLabel ACCBTNs() { return null; }

    private void initializeLists() {
        workoutCheckBoxes = Arrays.asList(checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16);
        challengeCheckBoxes = Arrays.asList(Chall1, Chall2, Chall3, Chall4);
    }

    private void setupPlaceholders() {
        if (CheckUsernameField != null)
            CheckUsernameField.setText("Username");
        if (CheckPassField != null)
            CheckPassField.setText("Password");
        if (CreatePassTextField != null)
            CreatePassTextField.setText("Password");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {}
            new Test();
        });
    }
}