import java.time.LocalDate;
import java.util.UUID;

public class Workout {
    private String workoutId; // Restored
    private String workoutName;
    private String type; // "Cardio" or "Strength"
    private int durationMins;
    private int reps;
    private int sets;
    private String intensity;
    private double caloriesBurned;
    private LocalDate date;

    // Constructor 1: Cardio
    public Workout(String name, int duration, String intensity) {
        this.workoutId = UUID.randomUUID().toString(); // Auto-generate ID
        this.workoutName = name;
        this.type = "Cardio";
        this.durationMins = duration;
        this.reps = 0;
        this.sets = 0;
        this.intensity = intensity;
        this.date = LocalDate.now();
        calculateBurn();
    }

    // Constructor 2: Strength
    public Workout(String name, int reps, int sets, String intensity) {
        this.workoutId = UUID.randomUUID().toString(); // Auto-generate ID
        this.workoutName = name;
        this.type = "Strength";
        this.durationMins = sets * 3;
        this.reps = reps;
        this.sets = sets;
        this.intensity = intensity;
        this.date = LocalDate.now();
        calculateBurn();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void calculateBurn() {
        double multiplier = 4.0;
        if (intensity.equalsIgnoreCase("High")) multiplier = 10.0;
        else if (intensity.equalsIgnoreCase("Medium")) multiplier = 7.0;

        if (this.type.equals("Cardio")) {
            this.caloriesBurned = durationMins * multiplier;
        } else {
            double totalReps = reps * sets;
            this.caloriesBurned = totalReps * (multiplier / 2.0);
        }
    }

    // --- Update Methods (Restored) ---

    /**
     * Updates a Cardio workout.
     */
    public void updateWorkout(int newDuration, String newIntensity) {
        if (!this.type.equals("Cardio")) {
            System.out.println("Error: Cannot update Strength workout with Cardio parameters.");
            return;
        }
        this.durationMins = newDuration;
        this.intensity = newIntensity;
        calculateBurn(); // Recalculate stats automatically
        System.out.println(">> Workout Updated: " + workoutName);
    }

    /**
     * Updates a Strength workout.
     */
    public void updateWorkout(int newReps, int newSets, String newIntensity) {
        if (!this.type.equals("Strength")) {
            System.out.println("Error: Cannot update Cardio workout with Strength parameters.");
            return;
        }
        this.reps = newReps;
        this.sets = newSets;
        this.durationMins = newSets * 3; // Recalculate estimated duration
        this.intensity = newIntensity;
        calculateBurn(); // Recalculate stats automatically
        System.out.println(">> Workout Updated: " + workoutName);
    }

    public void awardXP(User user) {
        user.logWorkout(this);
    }

    // Getters
    public String getWorkoutId() { return workoutId; } // Added Getter
    public LocalDate getDate() { return date; }
    public int getDurationMins() { return durationMins; }
    public String getIntensity() { return intensity; }
    public double getCaloriesBurned() { return caloriesBurned; }
    public String getWorkoutName() { return workoutName; }
    public String getType() { return type; }
    public int getReps() { return reps; }
    public int getSets() { return sets; }
}