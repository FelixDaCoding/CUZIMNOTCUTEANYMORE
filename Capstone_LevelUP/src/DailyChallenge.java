import java.util.Date;

public class DailyChallenge {
    private String challengeId;
    private String challengeName;
    private String description;
    private double targetValue;
    private double currentProgress; // Tracks accumulated value (Reps, Mins, Calories)
    private String targetType; // "Calories", "Run_Duration", "Pushups_Reps", etc.
    private Date assignedDate;
    private boolean completed;

    public DailyChallenge(String name, String description, double targetValue, String targetType) {
        this.challengeName = name;
        this.description = description;
        this.targetValue = targetValue;
        this.targetType = targetType;
        this.assignedDate = new Date();
        this.completed = false;
        this.currentProgress = 0;
    }

    /**
     * Adds to the progress and checks for completion.
     * Call this every time a relevant activity is logged.
     */
    public void addToProgress(double amount, User user) {
        if (completed) return;

        this.currentProgress += amount;
        System.out.println(">> Challenge Progress: " + (int)currentProgress + " / " + (int)targetValue + " (" + targetType + ")");

        if (this.currentProgress >= this.targetValue) {
            markComplete(user);
        }
    }

    /**
     * Resets the counter. Call this at Midnight.
     */
    public void resetProgress() {
        this.currentProgress = 0;
        this.completed = false;
        this.assignedDate = new Date(); // Update date to today
        System.out.println(">> Daily Challenge Reset.");
    }

    public void recordProgress(User user) {
        // Placeholder for automated backend checks
    }

    public void markComplete(User user) {
        if (!completed) {
            this.completed = true;
            System.out.println(">> Challenge Completed! Reward: 500 XP");
            // In a real implementation, user.gainXp(500);
        }
    }

    public boolean checkFailure(User user) {
        return false; // Placeholder for midnight check
    }

    public void applyDailyPenalty(User user) {
        // Placeholder
    }

    // Getters
    public String getChallengeName() { return challengeName; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public double getTargetValue() { return targetValue; }
    public String getTargetType() { return targetType; }
    public double getCurrentProgress() { return currentProgress; }
}
