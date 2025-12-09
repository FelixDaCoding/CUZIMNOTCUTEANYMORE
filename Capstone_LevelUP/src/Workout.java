public class Workout {
    private String workoutId;
    private String workoutName;
    private String type; // "Cardio" or "Strength"
    private int durationMins;
    private int reps;
    private int sets;
    private String intensity; // "High", "Medium", "Low"
    private double caloriesBurned;

    // Constructor 1: Cardio (Duration based)
    public Workout(String name, int duration, String intensity) {
        this.workoutName = name;
        this.type = "Cardio";
        this.durationMins = duration;
        this.reps = 0;
        this.sets = 0;
        this.intensity = intensity;
        this.caloriesBurned = 0;
        calculateBurn();
    }

    // Constructor 2: Strength (Rep based)
    public Workout(String name, int reps, int sets, String intensity) {
        this.workoutName = name;
        this.type = "Strength";
        this.durationMins = sets * 3; // Estimate: 1 set takes ~3 mins (including rest)
        this.reps = reps;
        this.sets = sets;
        this.intensity = intensity;
        this.caloriesBurned = 0;
        calculateBurn();
    }

    public void calculateBurn() {
        double multiplier = 4.0; // Default Low
        if (intensity.equalsIgnoreCase("High")) multiplier = 10.0;
        else if (intensity.equalsIgnoreCase("Medium")) multiplier = 7.0;

        if (this.type.equals("Cardio")) {
            // Cardio Formula: Duration * Intensity
            this.caloriesBurned = durationMins * multiplier;
        } else {
            // Strength Formula: Total Reps * Effort
            // (Simplified logic: 10 reps = ~1 min of high intensity)
            double totalReps = reps * sets;
            this.caloriesBurned = totalReps * (multiplier / 2.0);
        }
    }

    public void awardXP(User user) {
        user.logWorkout(this);
    }

    // Getters
    public int getDurationMins() { return durationMins; }
    public String getIntensity() { return intensity; }
    public double getCaloriesBurned() { return caloriesBurned; }
    public String getWorkoutName() { return workoutName; }
    public String getType() { return type; }
    public int getReps() { return reps; }
    public int getSets() { return sets; }
}
