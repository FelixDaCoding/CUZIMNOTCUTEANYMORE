public class Meal {
    // Attributes
    private String mealId;
    private String mealName;
    private int calories;
    private String mealType; // "Breakfast", "Lunch", etc.
    private boolean isHealthy;

    public Meal(String name, int calories, boolean isHealthy) {
        this.mealName = name;
        this.calories = calories;
        this.isHealthy = isHealthy;
    }

    // Methods from Diagram
    public void calculateCalories() {
        // TODO: Sum up ingredients (if we had them)
    }

    public void classifyMeal() {
        // TODO: Logic to determine if "Healthy" or not
    }

    public void updateMeal() {
        // TODO: Edit meal entry
    }
}