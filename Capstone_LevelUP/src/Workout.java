public class Workout {
    // Attributes
    private String workoutId;
    private String workoutName;
    private int durationMins;
    private String intensity; // e.g., "High", "Medium", "Low"
    private double caloriesBurned;

    public Workout(String name, int duration, String intensity) {
        this.workoutName = name;
        this.durationMins = duration;
        this.intensity = intensity;
    }

    // Methods from Diagram
    public void calculateBurn() {
        // TODO: Math based on intensity * duration
    }

    public void awardXP(User user) {
        // TODO: Add XP to user based on difficulty
    }

    public void updateWorkout() {
        // TODO: Modify workout details
    }
}