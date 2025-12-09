import java.util.ArrayList;
import java.util.List;

public class User {
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
        this.currentDailyChallenge = null;

        systemAccounts.add(this);
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
    public void setDailyChallenge(DailyChallenge challenge) {
        this.currentDailyChallenge = challenge;
        System.out.println(">> New Daily Challenge: " + challenge.getChallengeName());
    }

    // Method to check and update daily challenge
    public void updateDailyChallenge() {
        if (currentDailyChallenge != null && !currentDailyChallenge.isCompleted()) {
            // Check if challenge is from today
            if (!currentDailyChallenge.getAssignedDate().equals(java.time.LocalDate.now())) {
                System.out.println(">> Daily challenge expired, resetting...");
                currentDailyChallenge.resetProgress();
            }
        }
    }

    // Method to automatically progress daily challenge based on workout
    public void progressDailyChallengeWithWorkout(Workout workout) {
        if (currentDailyChallenge == null || currentDailyChallenge.isCompleted()) {
            return;
        }

        DailyChallenge.TargetType targetType = currentDailyChallenge.getTargetType();
        double progressAmount = 0;

        switch (targetType) {
            case CALORIES:
                progressAmount = workout.getCaloriesBurned();
                break;
            case RUN_DURATION:
            case JOG_MINS:
                if (workout.getWorkoutName().toLowerCase().contains("run") ||
                        workout.getWorkoutName().toLowerCase().contains("jog")) {
                    progressAmount = workout.getDurationMins();
                }
                break;
            case PUSHUPS_REPS:
                if (workout.getWorkoutName().toLowerCase().contains("pushup")) {
                    progressAmount = workout.getReps() * workout.getSets();
                }
                break;
            case CRUNCHES_REPS:
                if (workout.getWorkoutName().toLowerCase().contains("crunch")) {
                    progressAmount = workout.getReps() * workout.getSets();
                }
                break;
            case SQUATS_REPS:
                if (workout.getWorkoutName().toLowerCase().contains("squat")) {
                    progressAmount = workout.getReps() * workout.getSets();
                }
                break;
        }

        if (progressAmount > 0) {
            currentDailyChallenge.addToProgress(progressAmount, this);
        }
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

    public int logMeal(Meal meal) {
        mealLog.add(meal);
        int gainedXP = 0;
        if (meal.isHealthy()) {
            gainedXP = 50;
            this.xp += gainedXP;
            levelUp();

            // Check quests after meal
            checkAndClaimQuests();
        }
        return gainedXP;
    }

    public boolean levelUp() {
        int xpThreshold = this.level * 500;
        if (this.xp >= xpThreshold) {
            this.level++;
            System.out.println(">> *** LEVEL UP! You are now Level " + this.level + " ***");

            // Check quests after level up
            checkAndClaimQuests();
            return true;
        }
        return false;
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