import java.time.LocalDateTime;

public class Quest {
    private String questId;
    private String questName;
    private int rewardXP;
    private String questType; // "LevelUp", "Streak", "WorkoutCount"
    private int targetAmount; // Generic target (e.g., Level 5, Streak 10)
    private LocalDateTime timeLimit;
    private boolean isClaimed;

    public Quest(String name, int reward, String type, int target) {
        this.questName = name;
        this.rewardXP = reward;
        this.questType = type;
        this.targetAmount = target;
        this.isClaimed = false;
        this.timeLimit = LocalDateTime.now().plusDays(7); // Default 1 week
    }

    public boolean checkCompletion(User user) {
        if (isClaimed) return true;

        boolean conditionMet = false;

        switch (questType) {
            case "LevelUp":
                conditionMet = user.getLevel() >= targetAmount;
                break;
            case "Streak":
                conditionMet = user.getStreak().getCurrentStreak() >= targetAmount;
                break;
            // Add more cases as needed
        }

        return conditionMet;
    }

    public void claimReward(User user) {
        if (!isClaimed && checkCompletion(user)) {
            // Since User doesn't have a public addXP, we assume logWorkout handles it
            // or we print for now. Ideally User has public void gainXP(int amount).
            System.out.println("Quest Claimed! +" + rewardXP + " XP");
            isClaimed = true;
        }
    }

    public void resetQuest() {
        this.isClaimed = false;
    }

    public String getQuestName() { return questName; }
    public int getRewardXP() { return rewardXP; }
}