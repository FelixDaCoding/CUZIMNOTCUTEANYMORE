import java.io.Serializable;

public class ChallengeTask implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // This method was missing!
    public String getProgressDisplay() {
        return current + "/" + target + " " + unit;
    }

    // Getters
    public String getTaskName() { return taskName; }
    public int getTarget() { return target; }
    public int getCurrent() { return current; }
}