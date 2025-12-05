import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private String name;
    private String username;
    private String password;
    private int xp;
    private int level;
    private int penaltyPoints;

    // Relationships
    private final List<Workout> workoutLog;
    private final List<Meal> mealLog;
    private final List<Reward> earnedRewards;
    private final List<Penalty> activePenalties;
    private final List<Quest> activeQuests;
    private DailyChallenge currentDailyChallenge;
    private Streak currentStreak;

    // Static in-memory store
    private static final List<User> systemAccounts = new ArrayList<>();

    public User(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;

        this.xp = 0;
        this.level = 1;
        this.penaltyPoints = 0;

        this.workoutLog = new ArrayList<>();
        this.mealLog = new ArrayList<>();
        this.earnedRewards = new ArrayList<>();
        this.activePenalties = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.currentStreak = new Streak();

        // Auto-assign challenge on creation
        generateDailyChallenge();

        systemAccounts.add(this);
    }

    // --- Data Aggregation for UI ---

    /**
     * Returns the total minutes of workouts done in the last 7 days.
     * Used for the big "180 Workout Minutes" number on the Home Page.
     */
    public int getTotalWeeklyMinutes() {
        int total = 0;
        LocalDate today = LocalDate.now();
        for (Workout w : workoutLog) {
            LocalDate wDate = w.getDate();
            if (!wDate.isBefore(today.minusDays(6)) && !wDate.isAfter(today)) {
                total += w.getDurationMins();
            }
        }
        return total;
    }

    // Basic getters and setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    private void setPassword(String password) { this.password = password; }

    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getPenaltyPoints() { return penaltyPoints; }
    public void setPenaltyPoints(int penaltyPoints) { this.penaltyPoints = penaltyPoints; }

    public DailyChallenge getDailyChallenge() { return currentDailyChallenge; }

    // Manual setter if needed, but logic now prefers generation
    public void setDailyChallenge(DailyChallenge challenge) {
        this.currentDailyChallenge = challenge;
        System.out.println(">> New Daily Challenge: " + challenge.getChallengeName());
    }

    // --- Daily Challenge Logic ---

    /**
     * Checks if the challenge is outdated OR if the difficulty doesn't match the user's level.
     */
    /**
     * Checks if a new challenge is needed.
     * FIX: Ensures we do NOT overwrite an existing challenge for the current day.
     */
    public void updateDailyChallenge() {
        LocalDate today = LocalDate.now();

        // 1. SAFETY CHECK:
        // If we already have a challenge AND it is assigned for today...
        if (this.currentDailyChallenge != null &&
                this.currentDailyChallenge.getAssignedDate().equals(today)) {
            // ...STOP! Do not touch it. Keep the progress.
            return;
        }

        // 2. Only generate if it's null or from a previous date
        System.out.println(">> Generating new Daily Challenge for " + today.getDayOfWeek());
        generateDailyChallenge();
    }


    /**
     * Generates a specific challenge based on Day of Week + User Level.
     * Calculates specific Sets and Reps instructions.
     */
    private void generateDailyChallenge() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int scale = Math.max(1, this.level); // Difficulty scaler

        // --- CALCULATION LOGIC ---
        // We define the totals, then break them down into Sets x Reps for the description

        int pushupsTotal = 10 * scale;
        int situpsTotal = 20 * scale;
        int squatsTotal = 15 * scale;
        int stepsTotal = 10000 + (500 * (scale - 1));

        // Helper formatting for "3 Sets of X"
        String pushupInst = formatSetsReps(pushupsTotal, "Push-Ups");
        String situpInst = formatSetsReps(situpsTotal, "Sit-Ups");
        String squatInst = formatSetsReps(squatsTotal, "Squats");

        // Build the text description
        String routineDesc = String.format(
                "\n[MAIN ROUTINE]\n- %s\n- %s\n- %s\n- %d Steps (Total)",
                pushupInst, situpInst, squatInst, stepsTotal
        );

        // 1. Create Challenge Object
        DailyChallenge challenge = new DailyChallenge(
                "Daily Grind",
                "Follow the Sets/Reps instructions below.",
                100,
                DailyChallenge.TargetType.COMPOSITE,
                scale,
                100 + (scale * 20),
                10
        );

        // 2. Add Tasks (Tracking Total Numbers)
        challenge.addTask("Push-Ups", pushupsTotal, "Reps");
        challenge.addTask("Sit-Ups", situpsTotal, "Reps");
        challenge.addTask("Squats", squatsTotal, "Reps");
        challenge.addTask("Steps", stepsTotal, "Steps");

        // 3. Add Specific Daily Focus & Update Description
        String dailyFocus = "";

        switch (today) {
            case MONDAY: // Cardio
                dailyFocus = "Monday Cardio: 1 Run (Continuous)";
                challenge.addTask("Running", 15 + (5 * scale), "Mins");
                break;
            case TUESDAY: // Lower Body
                int lungeTotal = 20 * scale;
                int calfTotal = 25 * scale;
                dailyFocus = "Tuesday Legs:\n- " + formatSetsReps(lungeTotal, "Lunges") +
                        "\n- " + formatSetsReps(calfTotal, "Calf Raises");
                challenge.addTask("Lunges", lungeTotal, "Reps");
                challenge.addTask("Calf Raises", calfTotal, "Reps");
                break;
            case WEDNESDAY: // Upper Body
                int dipTotal = 10 * scale;
                dailyFocus = "Wednesday Upper:\n- " + formatSetsReps(dipTotal, "Tricep Dips") +
                        "\n- Plank: " + (1 + (scale/3)) + " Sets of 60s";
                challenge.addTask("Tricep Dips", dipTotal, "Reps");
                challenge.addTask("Plank", 1 + (scale / 2), "Mins");
                break;
            case THURSDAY: // Active Rest
                dailyFocus = "Thursday Recovery: 1 Session of Yoga";
                challenge.addTask("Yoga", 15, "Mins");
                break;
            case FRIDAY: // Glutes
                int gluteTotal = 20 * scale;
                int legRaiseTotal = 15 * scale;
                dailyFocus = "Friday Glutes:\n- " + formatSetsReps(gluteTotal, "Glute Bridges") +
                        "\n- " + formatSetsReps(legRaiseTotal, "Side Leg Raises");
                challenge.addTask("Glute Bridges", gluteTotal, "Reps");
                challenge.addTask("Side Leg Raises", legRaiseTotal, "Reps");
                break;
            case SATURDAY: // Strength
                int burpeeTotal = 5 * scale;
                int pullTotal = Math.max(1, 2 * scale);
                dailyFocus = "Saturday Strength:\n- " + formatSetsReps(burpeeTotal, "Burpees") +
                        "\n- " + formatSetsReps(pullTotal, "Pull-Ups");
                challenge.addTask("Burpees", burpeeTotal, "Reps");
                challenge.addTask("Pull-Ups", pullTotal, "Reps");
                break;
            case SUNDAY: // Rest
                dailyFocus = "Sunday Rest: Light Walking";
                challenge.addTask("Walking", 20, "Mins");
                break;
        }

        // Combine everything into the description
        challenge.updateDescription(dailyFocus + "\n" + routineDesc);
        this.currentDailyChallenge = challenge;
    }

    // Helper to calculate sets (aiming for 10-20 reps per set)
    private String formatSetsReps(int total, String name) {
        int sets = 3; // Default to 3 sets

        // Adjust sets for high volume
        if (total > 60) sets = 4;
        if (total > 100) sets = 5;

        // Adjust for low volume
        if (total < 10) sets = 1;

        int reps = total / sets;
        int remainder = total % sets; // Distribute remainder if needed (simplified here)

        if (sets == 1) return total + " " + name;
        return sets + " Sets of " + reps + " " + name;
    }

    public Streak getCurrentStreak() { return currentStreak; }

    // Authentication methods
    public boolean checkPassword(String inputPass) {
        return this.password.equals(inputPass);
    }

    public static boolean usernameExists(String username) {
        return systemAccounts.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public static User authenticate(String username, String password) {
        return systemAccounts.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password))
                .findFirst()
                .orElse(null);
    }

    public static User register(String name, String username, String password) {
        if (usernameExists(username)) {
            return null;
        }
        String newId = "U" + (systemAccounts.size() + 1);
        return new User(newId, name, username, password);
    }

    // Business logic methods
    public int logWorkout(Workout workout) {
        workoutLog.add(workout);

        // Update streak
        if (currentStreak != null) {
            currentStreak.updateStreak(this);
        }

        // Update daily challenge
        // Ensure we have the correct challenge for the day before processing
        updateDailyChallenge();
        progressDailyChallengeWithWorkout(workout);

        double calories = workout.getCaloriesBurned();
        int gainedXP = 0;
        if (calories >= 20) {
            gainedXP = (int) (calories * 0.5);
            if (currentStreak != null && currentStreak.getCurrentStreak() > 0) {
                gainedXP = currentStreak.applyMultiplier(gainedXP);
            }
            this.xp += gainedXP;
            levelUp();

            // Check quests after workout
            checkAndClaimQuests();
        } else {
            System.out.println(">> Effort too low for XP (Min 20 kcal burn required).");
        }
        return gainedXP;
    }

    // In your User class, update the logMeal method:
    public int logMeal(Meal meal) {
        mealLog.add(meal);
        int gainedXP = 0;
        if (meal.isHealthy()) {
            // Base XP for healthy meal + bonus for good nutrition
            gainedXP = 50 + (meal.getTotalCalories() / 100); // +1 XP per 100 calories
            this.xp += gainedXP;
            levelUp();

            // Check quests
            checkAndClaimQuests();
        } else {
            System.out.println(">> Unhealthy meal logged - no XP gained. Try for healthier options next time!");
        }
        return gainedXP;
    }

    public boolean levelUp() {
        int xpThreshold = this.level * 500;
        if (this.xp >= xpThreshold) {
            this.level++;
            System.out.println(">> *** LEVEL UP! You are now Level " + this.level + " ***");

            // Regenerate challenge if level up changes difficulty mid-day?
            // Better to keep current one until next day to avoid confusion.

            // Check quests after level up
            checkAndClaimQuests();
            return true;
        }
        return false;
    }

    // Method to automatically progress daily challenge based on workout
    public void progressDailyChallengeWithWorkout(Workout workout) {
        if (currentDailyChallenge == null || currentDailyChallenge.isCompleted()) {
            return;
        }
        // Delegate completely to the Challenge's internal logic
        currentDailyChallenge.processWorkout(workout, this);
    }

    public void applyPenalty(Penalty penalty) {
        activePenalties.add(penalty);
        penalty.applyTo(this);
    }

    public void addReward(Reward reward) {
        earnedRewards.add(reward);
        System.out.println(">> Reward earned: " + reward.getRewardName());
    }

    // Quest management methods
    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        System.out.println(">> New quest added: " + quest.getQuestName());
    }

    public List<Quest> getActiveQuests() {
        return new ArrayList<>(activeQuests);
    }

    public void checkAndClaimQuests() {
        for (Quest quest : activeQuests) {
            if (quest.checkCompletion(this) && !quest.isClaimed()) {
                boolean claimed = quest.claimReward(this);
                if (claimed) {
                    System.out.println(">> Successfully claimed quest: " + quest.getQuestName());
                }
            }
        }
    }

    // XP management method for quest rewards
    public void gainXP(int amount) {
        this.xp += amount;
        System.out.println(">> Gained " + amount + " XP! Total XP: " + this.xp);
        levelUp(); // Check if level up occurs
    }

    // Additional getters for logs and rewards
    public List<Workout> getWorkoutLog() { return new ArrayList<>(workoutLog); }
    public List<Meal> getMealLog() { return new ArrayList<>(mealLog); }
    public List<Reward> getEarnedRewards() { return new ArrayList<>(earnedRewards); }
    public List<Penalty> getActivePenalties() { return new ArrayList<>(activePenalties); }

    // Method to get total workouts completed
    public int getTotalWorkouts() {
        return workoutLog.size();
    }

    // Method to get total healthy meals
    public int getTotalHealthyMeals() {
        return (int) mealLog.stream().filter(Meal::isHealthy).count();
    }

    // Method to display user stats
    public void displayStats() {
        System.out.println("\n=== USER STATS ===");
        System.out.println("Level: " + level);
        System.out.println("XP: " + xp + "/" + (level * 500));
        System.out.println("Workouts Completed: " + getTotalWorkouts());
        System.out.println("Healthy Meals: " + getTotalHealthyMeals());
        System.out.println("Current Streak: " + currentStreak.getCurrentStreak() + " days");
        System.out.println("Active Quests: " + activeQuests.size());
        if (currentDailyChallenge != null) {
            System.out.println("Daily Challenge: " + currentDailyChallenge.getChallengeName() +
                    " (" + currentDailyChallenge.getProgressString() + ")");
        }
    }
}