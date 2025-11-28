import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class User {
    private String userId;
    private String name;
    private int xp;
    private int level;
    private int penaltyPoints; // Restored
    private String username;
    private String password;

    public static List<User> systemAccounts = new ArrayList<>();

    // Lists
    private ArrayList<Workout> workoutLog;
    private List<Meal> mealLog;
    private List<Reward> earnedRewards;
    private List<Penalty> activePenalties; // Restored

    // Objects
    private DailyChallenge currentDailyChallenge;
    private Streak currentStreak;

    public User(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.xp = 0;
        this.level = 1;
        this.penaltyPoints = 0; // Init

        this.workoutLog = new ArrayList<>();
        this.mealLog = new ArrayList<>();
        this.earnedRewards = new ArrayList<>();
        this.activePenalties = new ArrayList<>(); // Init
        this.currentStreak = new Streak();

        systemAccounts.add(this);
    }

    // --- Core Logic ---

    public void applyPenalty(Penalty penalty) {
        activePenalties.add(penalty);
        this.penaltyPoints++;

        System.out.println(">> PENALTY APPLIED: " + penalty.describe());

        // Immediate Effect Logic (optional expansion)
        // if (penalty.getType().equals("XP_DEDUCTION")) {
        //     this.xp -= penalty.getMagnitude();
        // }
    }

    public void addReward(Reward reward) {
        earnedRewards.add(reward);
    }

    public void addWorkout(Workout workout) {
        logWorkout(workout);
    }

    public int logWorkout(Workout workout) {
        workoutLog.add(workout);
        double calories = workout.getCaloriesBurned();
        int gainedXP = (calories >= 20) ? (int) (calories * 0.5) : 0;

        if (currentStreak != null && currentStreak.getCurrentStreak() > 0) {
            gainedXP = currentStreak.applyMultiplier(gainedXP);
        }
        this.xp += gainedXP;
        levelUp();
        return gainedXP;
    }

    public int logMeal(Meal meal) {
        mealLog.add(meal);
        int gainedXP = meal.isHealthy() ? 50 : 0;
        this.xp += gainedXP;
        levelUp();
        return gainedXP;
    }

    public boolean levelUp() {
        int xpThreshold = this.level * 500;
        if (this.xp >= xpThreshold) {
            this.level++;
            return true;
        }
        return false;
    }

    // --- Data Aggregation for UI ---

    public int getTotalWeeklyMinutes() {
        int total = 0;
        int[] weekly = getWeeklyWorkoutMinutes();
        for (int mins : weekly) total += mins;
        return total;
    }

    public int[] getWeeklyWorkoutMinutes() {
        int[] weeklyMinutes = new int[7];
        LocalDate today = LocalDate.now();

        for (Workout w : workoutLog) {
            LocalDate wDate = w.getDate();
            if (!wDate.isBefore(today.minusDays(6)) && !wDate.isAfter(today)) {
                int daysAgo = java.time.Period.between(wDate, today).getDays();
                int index = 6 - daysAgo;
                if (index >= 0 && index < 7) {
                    weeklyMinutes[index] += w.getDurationMins();
                }
            }
        }
        return weeklyMinutes;
    }

    public Map<String, Integer> getDailyBreakdown() {
        Map<String, Integer> breakdown = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Workout w : workoutLog) {
            if (w.getDate().equals(today)) {
                String name = w.getWorkoutName();
                int amount = w.getType().equals("Cardio") ? w.getDurationMins() : (w.getReps() * w.getSets());
                breakdown.put(name, breakdown.getOrDefault(name, 0) + amount);
            }
        }
        return breakdown;
    }

    // --- Auth & Static Methods ---

    public static User register(String name, String username, String password) {
        if (checkUser(username)) return null;
        return new User("U" + (systemAccounts.size() + 1), name, username, password);
    }
    public static boolean checkUser(String inputUsername) {
        for (User u : systemAccounts) if (u.username.equalsIgnoreCase(inputUsername)) return true;
        return false;
    }
    public static User authenticate(String inputUser, String inputPass) {
        for (User u : systemAccounts) {
            if (u.username.equalsIgnoreCase(inputUser) && u.checkPass(inputPass)) return u;
        }
        return null;
    }
    public boolean checkPass(String inputPass) { return this.password.equals(inputPass); }

    // --- Getters ---

    public String getName() { return name; }
    public String getUserId() { return userId; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getPenaltyPoints() { return penaltyPoints; }
    public Streak getStreak() { return currentStreak; }
    public DailyChallenge getDailyChallenge() { return currentDailyChallenge; }
    public void setDailyChallenge(DailyChallenge challenge) { this.currentDailyChallenge = challenge; }
    public List<Reward> getEarnedRewards() { return earnedRewards; }
    public List<Penalty> getActivePenalties() { return activePenalties; }
}