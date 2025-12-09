import java.time.LocalDate;

public class DailyChallenge {
    public enum TargetType {
        CALORIES, RUN_DURATION, PUSHUPS_REPS, CRUNCHES_REPS, SQUATS_REPS, JOG_MINS
    }

    private final String challengeId;
    private final String challengeName;
    private final String description;
    private final double targetValue;
    private double currentProgress;
    private final TargetType targetType;
    private LocalDate assignedDate;
    private boolean completed;

    public DailyChallenge(String challengeId, String name, String description, double targetValue, TargetType targetType) {
        this.challengeId = challengeId;
        this.challengeName = name;
        this.description = description;
        this.targetValue = targetValue;
        this.targetType = targetType;
        this.assignedDate = LocalDate.now();
        this.completed = false;
        this.currentProgress = 0;
    }

    /**
     * Adds to the progress and checks for completion.
     * Call this each time a relevant activity is logged.
     */
    public void addToProgress(double amount, User user) {
        if (completed) return;

        this.currentProgress += amount;
        System.out.printf(">> Challenge Progress: %.0f / %.0f (%s)%n", currentProgress, targetValue, targetType);

        if (this.currentProgress >= this.targetValue) {
            markComplete(user);
        }
    }

    /**
     * Resets the progress and updates assigned date, typically called at midnight.
     */
    public void resetProgress() {
        this.currentProgress = 0;
        this.completed = false;
        this.assignedDate = LocalDate.now();
        System.out.println(">> Daily Challenge Reset.");
    }

    private void markComplete(User user) {
        if (!completed) {
            this.completed = true;
            System.out.println(">> Challenge Completed! Reward: 500 XP");
            // Use the gainXP method that exists in User class
            user.gainXP(500);
        }
    }

    // Additional getters
    public String getChallengeId() { return challengeId; }
    public String getChallengeName() { return challengeName; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public double getTargetValue() { return targetValue; }
    public TargetType getTargetType() { return targetType; }
    public double getCurrentProgress() { return currentProgress; }
    public LocalDate getAssignedDate() { return assignedDate; }

    public String getProgressString() {
        return String.format("%.0f/%.0f", currentProgress, targetValue);
    }
}