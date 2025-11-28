public class ChallengeTask {
    private String taskName; // e.g. "Push Ups"
    private int target;      // e.g. 15
    private int current;     // e.g. 0
    private String unit;     // e.g. "Reps", "Mins", "Km", "Kcal"

    public ChallengeTask(String taskName, int target, String unit) {
        this.taskName = taskName;
        this.target = target;
        this.unit = unit;
        this.current = 0;
    }

    public void addProgress(int amount) {
        this.current += amount;
        if (this.current > this.target) {
            this.current = this.target; // Cap at max
        }
    }

    public boolean isComplete() {
        return current >= target;
    }

    public String getStatus() {
        return current + "/" + target + " " + unit;
    }

    // Getters
    public String getTaskName() { return taskName; }
    public int getTarget() { return target; }
    public int getCurrent() { return current; }
}