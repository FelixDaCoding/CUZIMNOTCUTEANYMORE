import java.util.Date;

public class DailyChallenge {
    // Attributes
    private String challengeId;
    private String challengeName;
    private String description;
    private double targetValue; // e.g., 500 (calories) or 30 (minutes)
    private Date assignedDate;
    private boolean completed;

    // Methods from Diagram
    public void recordProgress(User user) {
        // TODO: Check user logs against targetValue
    }

    public void markComplete(User user) {
        // TODO: Set completed = true, grant XP
        this.completed = true;
    }

    public void checkFailure(User user) {
        // TODO: Run at midnight. If !completed -> applyDailyPenalty
    }

    public void applyDailyPenalty(User user) {
        // TODO: Trigger user.applyPenalty() and reset streak
    }
}