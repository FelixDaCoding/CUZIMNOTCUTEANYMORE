import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Streak implements Serializable {

    private static final long serialVersionUID = 1L;

    private String streakId;
    private int currentStreak;
    private Date lastActiveDate;
    private double multiplier;

    public Streak() {
        this.currentStreak = 0;
        this.multiplier = 1.0;
        this.lastActiveDate = new Date(); // Defaults to now
    }

    public void updateStreak(User user) {
        Date today = new Date();
        long diffInMillies = Math.abs(today.getTime() - lastActiveDate.getTime());
        long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diffDays == 0) {
            // Already updated today, do nothing
            return;
        } else if (diffDays == 1) {
            // Perfect streak
            this.currentStreak++;
            this.multiplier += 0.1; // +10% bonus per day
        } else {
            // Missed a day
            resetStreak();
        }
        this.lastActiveDate = today;
    }

    public int applyMultiplier(int baseXP) {
        return (int) (baseXP * this.multiplier);
    }

    public void resetStreak() {
        this.currentStreak = 0;
        this.multiplier = 1.0;
    }

    public void penalizeStreak() {
        this.multiplier = Math.max(1.0, this.multiplier - 0.5); // Penalty reduces multiplier
    }

    public int getCurrentStreak() { return currentStreak; }
    public double getMultiplier() { return multiplier; }
}
