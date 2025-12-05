import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DailyChallenge implements Serializable {
    private static final long serialVersionUID = 1L;

    // Restored Enum for Main.java compatibility
    public enum TargetType {
        CALORIES, RUN_DURATION, PUSHUPS_REPS, CRUNCHES_REPS, SQUATS_REPS, JOG_MINS, COMPOSITE
    }

    private final String challengeId;
    private final String challengeName;
    private String description;
    private final double targetValue;
    private double currentProgress;
    private final TargetType targetType;
    private LocalDate assignedDate;
    private boolean completed;

    // UI Metadata
    private int difficulty;
    private int xpReward;
    private int streakBonus;

    // Composite Tasks
    private List<ChallengeTask> tasks;

    // Default constructor
    public DailyChallenge() {
        this("Default Challenge", "Default Desc", 100, TargetType.CALORIES);
    }

    // Simple Constructor (Single Target)
    public DailyChallenge(String name, String description, double targetValue, TargetType targetType) {
        this(name, description, targetValue, targetType, 1, 100, 1);
    }

    // Full Constructor
    public DailyChallenge(String name, String description, double targetValue, TargetType targetType, int difficulty, int xpReward, int streakBonus) {
        this.challengeId = UUID.randomUUID().toString();
        this.challengeName = name;
        this.description = description;
        this.targetValue = targetValue;
        this.targetType = targetType;
        this.difficulty = difficulty;
        this.xpReward = xpReward;
        this.streakBonus = streakBonus;
        this.assignedDate = LocalDate.now();
        this.completed = false;
        this.currentProgress = 0;
        this.tasks = new ArrayList<>();
    }

    // Constructor for backwards compatibility with Main.java
    public DailyChallenge(String id, String name, String description, double target, TargetType type) {
        this(name, description, target, type, 2, 200, 5); // Default rewards
    }

    public void addTask(String name, int target, String unit) {
        tasks.add(new ChallengeTask(name, target, unit));
    }

    /**
     * Smart update: Checks both composite tasks and main target.
     */
    public void processWorkout(Workout w, User user) {
        if (completed) return;

        // 1. Update Composite Tasks
        if (!tasks.isEmpty()) {
            boolean progressMade = false;
            for (ChallengeTask task : tasks) {
                if (task.getTaskName().equalsIgnoreCase("Calories")) {
                    task.addProgress((int) w.getCaloriesBurned());
                    progressMade = true;
                } else if (w.getWorkoutName().toLowerCase().contains(task.getTaskName().toLowerCase())) {
                    int amount = w.getType().equals("Cardio") ? w.getDurationMins() : (w.getReps() * w.getSets());
                    task.addProgress(amount);
                    progressMade = true;
                }
            }
            if (progressMade) checkCompositeCompletion(user);
        }

        // 2. Update Single Target (Fallback for old logic)
        else {
            double amount = 0;
            switch (targetType) {
                case CALORIES: amount = w.getCaloriesBurned(); break;
                case RUN_DURATION: if(w.getWorkoutName().contains("Run")) amount = w.getDurationMins(); break;
                case PUSHUPS_REPS: if(w.getWorkoutName().contains("Push")) amount = w.getReps() * w.getSets(); break;
                case CRUNCHES_REPS: if(w.getWorkoutName().contains("Crunch")) amount = w.getReps() * w.getSets(); break;
                case SQUATS_REPS: if(w.getWorkoutName().contains("Squat")) amount = w.getReps() * w.getSets(); break;
            }
            if (amount > 0) addToProgress(amount, user);
        }
    }

    // Helper for composite completion
    private void checkCompositeCompletion(User user) {
        for (ChallengeTask task : tasks) {
            if (!task.isComplete()) return;
        }
        markComplete(user);
    }

    public void updateDescription(String newDesc) {
        // We can't change the final field 'description' directly via reflection safely in all contexts,
        // but for this implementation, it's better to remove 'final' from the 'description' field declaration
        // at the top of the class, OR create a new field 'currentDescription'.

        // HOWEVER, to keep it simple and not break Serialization of existing objects:
        // Let's just assume you remove the 'final' keyword from 'private String description;' at the top.
        this.description = newDesc;
    }

    // Helper for single target completion
    public void addToProgress(double amount, User user) {
        if (completed) return;
        this.currentProgress += amount;
        if (this.currentProgress >= this.targetValue) {
            this.currentProgress = this.targetValue;
            markComplete(user);
        }
    }

    public void markComplete(User user) {
        if (!completed) {
            this.completed = true;
            System.out.println(">> Challenge Completed! Reward: " + xpReward + " XP");
            user.gainXP(xpReward);
            user.getCurrentStreak().updateStreak(user);
        }
    }

    public void resetProgress() {
        this.currentProgress = 0;
        this.completed = false;
        this.assignedDate = LocalDate.now();
        for(ChallengeTask t : tasks) t = new ChallengeTask(t.getTaskName(), t.getTarget(), "units"); // Reset tasks
    }

    // --- Timers ---
    public Duration countDownTimer() {
        LocalDateTime deadline = LocalDateTime.of(assignedDate.plusDays(1), LocalTime.MIDNIGHT);
        return Duration.between(LocalDateTime.now(), deadline);
    }

    public String displayTimer() {
        Duration timeLeft = countDownTimer();
        if (timeLeft.isZero() || timeLeft.isNegative()) return "Expired";
        return String.format("%02dh %02dm remaining", timeLeft.toHours(), timeLeft.toMinutesPart());
    }

    public String getProgressString() {
        return String.format("%.0f/%.0f", currentProgress, targetValue);
    }

    // Getters
    public String getChallengeId() { return challengeId; }
    public String getChallengeName() { return challengeName; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public double getTargetValue() { return targetValue; }
    public double getCurrentProgress() { return currentProgress; }
    public TargetType getTargetType() { return targetType; } // Restored
    public int getDifficulty() { return difficulty; }
    public int getXpReward() { return xpReward; }
    public int getStreakBonus() { return streakBonus; }
    public List<ChallengeTask> getTasks() { return tasks; }
    public LocalDate getAssignedDate() { return assignedDate; }
}