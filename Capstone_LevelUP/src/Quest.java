import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Quest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String questId;
    private String questName;
    private int rewardXP;
    private String questType; // "LevelUp", "Streak", "WorkoutCount", "HealthyMeals"
    private int targetAmount;
    private LocalDateTime timeLimit;
    private boolean isClaimed;

    public Quest(String name, int reward, String type, int target) {
        this.questName = name;
        this.rewardXP = reward;
        this.questType = type;
        this.targetAmount = target;
        this.isClaimed = false;
        this.timeLimit = LocalDateTime.now().plusDays(7);
    }

    public Quest(String questId, String name, int reward, String type, int target, LocalDateTime timeLimit, boolean isClaimed) {
        this.questId = questId;
        this.questName = name;
        this.rewardXP = reward;
        this.questType = type;
        this.targetAmount = target;
        this.timeLimit = timeLimit;
        this.isClaimed = isClaimed;
    }

    public boolean checkCompletion(User user) {
        if (isClaimed) return false; // Already claimed, not eligible for completion check
        if (LocalDateTime.now().isAfter(timeLimit)) {
            System.out.println(">> Quest '" + questName + "' has expired!");
            return false;
        }

        boolean conditionMet = false;

        switch (questType) {
            case "LevelUp":
                conditionMet = user.getLevel() >= targetAmount;
                break;
            case "Streak":
                conditionMet = user.getCurrentStreak().getCurrentStreak() >= targetAmount;
                break;
            case "WorkoutCount":
                conditionMet = user.getTotalWorkouts() >= targetAmount;
                break;
            case "HealthyMeals":
                conditionMet = user.getTotalHealthyMeals() >= targetAmount;
                break;
            case "XP":
                conditionMet = user.getXp() >= targetAmount;
                break;
            default:
                System.out.println(">> Unknown quest type: " + questType);
                return false;
        }

        if (conditionMet) {
            System.out.println(">> Quest '" + questName + "' completed! Ready to claim.");
        }

        return conditionMet;
    }

    public boolean claimReward(User user) {
        if (isClaimed) {
            System.out.println(">> Quest '" + questName + "' already claimed!");
            return false;
        }

        if (!checkCompletion(user)) {
            System.out.println(">> Quest '" + questName + "' not completed yet!");
            return false;
        }

        if (LocalDateTime.now().isAfter(timeLimit)) {
            System.out.println(">> Quest '" + questName + "' expired before claiming!");
            return false;
        }

        // Award XP to user
        user.gainXP(rewardXP);
        isClaimed = true;
        System.out.println(">> 🎉 Quest '" + questName + "' claimed! +" + rewardXP + " XP awarded.");
        return true;
    }

    public void resetQuest() {
        this.isClaimed = false;
        this.timeLimit = LocalDateTime.now().plusDays(7);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(timeLimit);
    }

    public String getTimeRemaining() {
        if (isClaimed) return "Claimed";
        if (isExpired()) return "Expired";

        java.time.Duration duration = java.time.Duration.between(LocalDateTime.now(), timeLimit);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        return days + "d " + hours + "h";
    }

    // Getters
    public String getQuestId() { return questId; }
    public String getQuestName() { return questName; }
    public int getRewardXP() { return rewardXP; }
    public String getQuestType() { return questType; }
    public int getTargetAmount() { return targetAmount; }
    public boolean isClaimed() { return isClaimed; }
    public LocalDateTime getTimeLimit() { return timeLimit; }

    public String getFormattedTimeLimit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return timeLimit.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("Quest: %s | Type: %s | Target: %d | Reward: %d XP | Status: %s | Time Left: %s",
                questName, questType, targetAmount, rewardXP,
                isClaimed ? "Claimed" : (isExpired() ? "Expired" : "Active"),
                getTimeRemaining());
    }
}