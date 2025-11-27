import java.util.Date;

public class DailyChallenge {
    private String challengeId;
    private String challengeName;
    private String description;
    private double targetValue; 
    private String targetType; // "Calories", "Workouts", "Meals"
    private Date assignedDate;
    private boolean completed;

    public DailyChallenge(String name, String description, double targetValue, String targetType) {
        this.challengeName = name;
        this.description = description;
        this.targetValue = targetValue;
        this.targetType = targetType;
        this.assignedDate = new Date();
        this.completed = false;
    }

    public void recordProgress(User user) {
        if (completed) return;

        // Check if user met the goal
        // NOTE: In a real app, this would iterate through today's logs.
        // Here is a simplified logic check:
        
        if (targetType.equals("Calories")) {
            // Sum calories from workouts
            // Implementation requires accessing User's workoutLog (via getter if added)
        }
        // For simplicity in this demo, we assume manual completion triggers:
        System.out.println("Checking progress for: " + challengeName);
    }

    public void markComplete(User user) {
        if (!completed) {
            this.completed = true;
            System.out.println("Challenge Completed! Reward: 500 XP");
            // Hardcoded reward for daily challenge
            // user.gainXp(500); // Assuming user has a direct addXP method or similar
        }
    }

    public boolean checkFailure(User user) {
        // Runs at midnight logic
        Date now = new Date();
        // If current date > assigned date AND !completed
        // return true (Failed)
        return false; // Placeholder
    }

    public void applyDailyPenalty(User user) {
        if (checkFailure(user)) {
            Penalty p = new Penalty("P01", "Daily Fail", 50, 24);
            user.applyPenalty(p);
            user.getStreak().resetStreak();
        }
    }
    
    // Getters for UI
    public String getChallengeName() { return challengeName; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
}
