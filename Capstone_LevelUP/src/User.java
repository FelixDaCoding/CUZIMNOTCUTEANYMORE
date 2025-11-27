import java.util.ArrayList;
import java.util.List;

public class User {
    // Attributes
    private String userId;
    private String name;
    private int xp;
    private int level;
    private int penaltyPoints;

    // Authentication Attributes
    private String username;
    private String password;

    // The "Local Database" - Stores all registered users in memory
    public static List<User> systemAccounts = new ArrayList<>();

    // Relationships
    private List<Workout> workoutLog;
    private List<Meal> mealLog;
    private List<Reward> earnedRewards;
    private List<Penalty> activePenalties;
    private List<Quest> activeQuests;

    // 1-to-1 Relationships
    private DailyChallenge currentDailyChallenge;
    private Streak currentStreak;

    // Constructor
    public User(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;

        this.xp = 0;
        this.level = 1;
        this.penaltyPoints = 0;

        // Initialize lists
        this.workoutLog = new ArrayList<>();
        this.mealLog = new ArrayList<>();
        this.earnedRewards = new ArrayList<>();
        this.activePenalties = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.currentStreak = new Streak();

        // Automatically add to "Local Database"
        systemAccounts.add(this);
    }

    // --- Authentication & Registration ---

    public static boolean checkUser(String inputUsername) {
        for (User u : systemAccounts) {
            if (u.username.equalsIgnoreCase(inputUsername)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkPass(String inputPass) {
        return this.password.equals(inputPass);
    }

    public static User authenticate(String inputUser, String inputPass) {
        for (User u : systemAccounts) {
            if (u.username.equalsIgnoreCase(inputUser)) {
                if (u.checkPass(inputPass)) {
                    return u;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Registers a new user if the username doesn't exist.
     * @return The new User object, or null if username is taken.
     */
    public static User register(String name, String username, String password) {
        if (checkUser(username)) {
            return null; // Username taken
        }
        // Generate a simple incremental ID
        String newId = "U" + (systemAccounts.size() + 1);
        return new User(newId, name, username, password);
    }

    // --- Static Init ---
    static {
        new User("U001", "Jin Woo", "imHIM", "bruv");
        new User("U002", "Cha Hae", "hunter_cha", "hunter1");
    }

    // --- Core Logic Methods ---

    /**
     * Logs a workout and returns the XP gained.
     * Dynamic Formula: XP = Calories * 0.5 (Min threshold: 50 kcal)
     */
    public int logWorkout(Workout workout) {
        workoutLog.add(workout);

        double calories = workout.getCaloriesBurned();
        int gainedXP = 0;

        // Minimum Effort Threshold (prevents 1 rep spamming)
        if (calories >= 20) {
            gainedXP = (int) (calories * 0.5); // 100 kcal = 50 XP
        } else {
            System.out.println(">> Effort too low for XP (Min 20 kcal burn required).");
        }

        // Apply Streak Multiplier
        if (currentStreak != null && currentStreak.getCurrentStreak() > 0) {
            gainedXP = currentStreak.applyMultiplier(gainedXP);
        }

        this.xp += gainedXP;
        levelUp();

        return gainedXP;
    }

    /**
     * Logs a meal and returns XP gained (if healthy).
     */
    public int logMeal(Meal meal) {
        mealLog.add(meal);

        int gainedXP = 0;
        if (meal.isHealthy()) {
            gainedXP = 50;
        }

        this.xp += gainedXP;
        levelUp();

        return gainedXP;
    }

    /**
     * Checks if the user has enough XP to level up.
     * Threshold Formula: Level * 500 XP required.
     * @return true if the level actually increased.
     */
    public boolean levelUp() {
        int xpThreshold = this.level * 500;

        if (this.xp >= xpThreshold) {
            this.level++;
            System.out.println(">> *** LEVEL UP! You are now Level " + this.level + " ***");
            return true;
        }
        return false;
    }

    public void applyPenalty(Penalty penalty) {
        activePenalties.add(penalty);
    }

    public void addReward(Reward reward) {
        earnedRewards.add(reward);
    }

    // Getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public Streak getStreak() { return currentStreak; }
    public DailyChallenge getDailyChallenge() { return currentDailyChallenge; }
    public void setDailyChallenge(DailyChallenge challenge) { this.currentDailyChallenge = challenge; }
}
