public class Meal {
    private String mealId;
    private String mealName;
    private int calories;
    private String mealType; // "Breakfast", "Lunch", "Dinner", "Snack"
    private boolean isHealthy;

    public Meal(String name, int calories, String mealType) {
        this.mealName = name;
        this.calories = calories;
        this.mealType = mealType;
        classifyMeal(); // Auto-classify
    }

    public void calculateCalories() {
        // In a real app, this would sum ingredients.
        // Here, we just assume the passed value is correct.
        System.out.println("Calories calculated: " + this.calories);
    }

    public void classifyMeal() {
        // Simple Game Logic: > 800 calories is "Unhealthy" unless it's a cheat meal
        if (this.calories > 800) {
            this.isHealthy = false;
        } else {
            this.isHealthy = true;
        }
    }

    public void updateMeal(int newCalories, String newType) {
        this.calories = newCalories;
        this.mealType = newType;
        classifyMeal();
    }

    // Getters
    public boolean isHealthy() { return isHealthy; }
    public String getMealName() { return mealName; }
    public int getCalories() { return calories; }
}
