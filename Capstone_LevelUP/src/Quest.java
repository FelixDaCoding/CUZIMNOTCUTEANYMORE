import java.time.LocalDateTime;

public class Quest {
    // Attributes
    private String questId;
    private String questName;
    private int rewardXP;
    private String questType; // "Weekly", "Special"
    private LocalDateTime timeLimit;

    // Methods from Diagram
    public boolean checkCompletion(User user) {
        // TODO: Validate requirements
        return false;
    }

    public void claimReward(User user) {
        // TODO: Give XP to user
    }

    public void resetQuest() {
        // TODO: Reset progress counters
    }
}