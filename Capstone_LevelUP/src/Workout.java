public class Workout {
    private String workoutId;
    private String workoutName;
    private int durationMins;
    private String intensity; // "High", "Medium", "Low"
    private double caloriesBurned;

    public Workout(String name, int duration, String intensity) {
        this.workoutName = name;
        this.durationMins = duration;
        this.intensity = intensity;
        this.caloriesBurned = 0;
        calculateBurn(); // Auto-calculate on creation
    }

    public void calculateBurn() {
        double multiplier = 4.0; // Default Low
        if (intensity.equalsIgnoreCase("High")) multiplier = 10.0;
        else if (intensity.equalsIgnoreCase("Medium")) multiplier = 7.0;
        
        this.caloriesBurned = durationMins * multiplier;
    }

    public void awardXP(User user) {
        // Calls the user to log this workout
        user.logWorkout(this);
    }

    public void updateWorkout(int newDuration, String newIntensity) {
        this.durationMins = newDuration;
        this.intensity = newIntensity;
        calculateBurn(); // Recalculate
    }

    // Getters needed for User logic
    public int getDurationMins() { return durationMins; }
    public String getIntensity() { return intensity; }
    public double getCaloriesBurned() { return caloriesBurned; }
    public String getWorkoutName() { return workoutName; }
}
