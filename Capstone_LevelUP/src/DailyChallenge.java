import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

public class DailyChallenge {
    private String challengeId; // Added ID
    private String challengeName;
    private String description;
    private List<ChallengeTask> tasks;
    private boolean completed;

    // UI Metadata
    private int difficulty;
    private int xpReward;
    private int streakBonus;

    public DailyChallenge(String name, String description, int difficulty, int xpReward, int streakBonus) {
        this.challengeId = UUID.randomUUID().toString(); // Auto-generate unique ID
        this.challengeName = name;
        this.description = description;
        this.difficulty = difficulty;
        this.xpReward = xpReward;
        this.streakBonus = streakBonus;
        this.tasks = new ArrayList<>();
        this.completed = false;
    }

    public void addTask(String name, int target, String unit) {
        tasks.add(new ChallengeTask(name, target, unit));
    }

    /**
     * The Brain: Takes a workout and distributes progress to relevant tasks.
     */
    public void processWorkout(Workout w, User user) {
        if (completed) return;

        boolean progressMade = false;

        for (ChallengeTask task : tasks) {
            // 1. Check Calorie Tasks
            if (task.getTaskName().equalsIgnoreCase("Calories")) {
                task.addProgress((int) w.getCaloriesBurned());
                progressMade = true;
            }
            // 2. Check Specific Activity Tasks (Name Match)
            else if (w.getWorkoutName().toLowerCase().contains(task.getTaskName().toLowerCase())) {
                int amount = 0;
                if (w.getType().equals("Cardio")) {
                    amount = w.getDurationMins();
                } else {
                    amount = w.getReps() * w.getSets();
                }
                task.addProgress(amount);
                progressMade = true;
            }
        }

        if (progressMade) {
            checkCompletion(user);
        }
    }

    private void checkCompletion(User user) {
        for (ChallengeTask task : tasks) {
            if (!task.isComplete()) return; // If any task is unfinished, stop.
        }

        // If we get here, ALL tasks are done
        markComplete(user);
    }

    /**
     * Official completion handler. Grants rewards.
     */
    public void markComplete(User user) {
        if (!completed) {
            this.completed = true;
            System.out.println("\n>> [!!!] DAILY CHALLENGE COMPLETE: " + challengeName);
            System.out.println(">> Rewards: " + xpReward + " XP, " + streakBonus + " Streak Score");

            // Apply Rewards
            user.getStreak().updateStreak(user);
            // user.addXp(xpReward); // Assuming XP is handled via logs or direct add
        }
    }

    /**
     * Midnight Check Logic.
     * Call this when the system detects a day change.
     */
    public void applyDailyPenalty(User user) {
        if (!completed) {
            System.out.println("\n>> [!!!] DAILY CHALLENGE FAILED: " + challengeName);

            // Create a Penalty Object
            Penalty failPenalty = new Penalty("P_DAILY_FAIL", "Daily Fail", 50, 24);
            user.applyPenalty(failPenalty);

            // Reset Streak
            user.getStreak().resetStreak();
            System.out.println(">> Streak has been reset to 0.");
        }
    }

    // Getters
    public String getChallengeId() { return challengeId; } // Added Getter
    public String getChallengeName() { return challengeName; }
    public boolean isCompleted() { return completed; }
    public int getDifficulty() { return difficulty; }
    public int getXpReward() { return xpReward; }
    public int getStreakBonus() { return streakBonus; }
    public List<ChallengeTask> getTasks() { return tasks; }
}