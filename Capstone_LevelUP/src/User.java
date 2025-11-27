import java.util.ArrayList;
import java.util.List;

public class User {
    // Attributes
    private String userId;
    private String name;
    private int xp;
    private int level;
    private int penaltyPoints;

    // Relationships (1-to-many lists based on the diagram)
    private List<Workout> workoutLog;
    private List<Meal> mealLog;
    private List<Reward> earnedRewards;
    private List<Penalty> activePenalties;
    private List<Quest> activeQuests;

    // 1-to-1 Relationships
    private DailyChallenge currentDailyChallenge;
    private Streak currentStreak;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.xp = 0;
        this.level = 1;
        this.penaltyPoints = 0;

        // Initialize lists
        this.workoutLog = new ArrayList<>();
        this.mealLog = new ArrayList<>();
        this.earnedRewards = new ArrayList<>();
        this.activePenalties = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.currentStreak = new Streak(); // User starts with a fresh streak
    }

    // Methods from Diagram
    public void logWorkout(Workout workout) {
        // TODO: Add to workoutLog and calculate XP
        workoutLog.add(workout);
    }

    public void logMeal(Meal meal) {
        // TODO: Add to mealLog and check health status
        mealLog.add(meal);
    }

    public void levelUp() {
        // TODO: Check XP threshold and increment level
    }

    public void applyPenalty(Penalty penalty) {
        // TODO: Add to activePenalties and apply effects
        activePenalties.add(penalty);
    }

    public void addReward(Reward reward) {
        // TODO: Add to earnedRewards if unlocked
        earnedRewards.add(reward);
    }

    // Getters for relationships (needed for other classes to interact)
    public Streak getStreak() { return currentStreak; }
    public DailyChallenge getDailyChallenge() { return currentDailyChallenge; }
    public void setDailyChallenge(DailyChallenge challenge) { this.currentDailyChallenge = challenge; }
}