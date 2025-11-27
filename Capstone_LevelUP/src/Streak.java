import java.util.Date;

public class Streak {
    // Attributes
    private String streakId;
    private int currentStreak;
    private Date lastActiveDate;
    private double multiplier; // e.g., 1.0, 1.2, 1.5

    public Streak() {
        this.currentStreak = 0;
        this.multiplier = 1.0;
        this.lastActiveDate = new Date();
    }

    // Methods from Diagram
    public void updateStreak(User user) {
        // TODO: Increment streak if called today
    }

    public void applyMultiplier(int baseXP) {
        // TODO: Return baseXP * multiplier
    }

    public void resetStreak() {
        this.currentStreak = 0;
        this.multiplier = 1.0;
    }

    public void penalizeStreak() {
        // TODO: Reduce multiplier or deduct days
    }
}