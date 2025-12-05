import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Meal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mealId;
    private String mealName;
    private String mealType; // "Breakfast", "Lunch", "Dinner", "Snack"
    private Map<String, FoodItem> foodItems; // Food name -> FoodItem with grams
    private int totalCalories;
    private boolean isHealthy;

    // Predefined food database with calories per 100g
    public static final Map<String, Integer> FOOD_DATABASE = new HashMap<>();
    static {
        // Protein (Healthy)
        FOOD_DATABASE.put("Chicken Breast", 165);
        FOOD_DATABASE.put("Salmon", 208);
        FOOD_DATABASE.put("Eggs", 155);
        FOOD_DATABASE.put("Tofu", 76);
        FOOD_DATABASE.put("Greek Yogurt", 59);
        FOOD_DATABASE.put("Chicken Wing", 203);
        FOOD_DATABASE.put("Chicken Leg", 184);
        FOOD_DATABASE.put("Beef Steak", 271);

        // Carbohydrates (Healthy)
        FOOD_DATABASE.put("Brown Rice", 111);
        FOOD_DATABASE.put("Quinoa", 120);
        FOOD_DATABASE.put("Sweet Potato", 86);
        FOOD_DATABASE.put("Oatmeal", 71);
        FOOD_DATABASE.put("Whole Wheat Bread", 247);
        FOOD_DATABASE.put("White Rice", 130);
        FOOD_DATABASE.put("Pasta", 131);
        FOOD_DATABASE.put("Potato", 77);

        // Vegetables (Healthy)
        FOOD_DATABASE.put("Broccoli", 34);
        FOOD_DATABASE.put("Spinach", 23);
        FOOD_DATABASE.put("Carrots", 41);
        FOOD_DATABASE.put("Bell Peppers", 31);
        FOOD_DATABASE.put("Lettuce", 15);
        FOOD_DATABASE.put("Tomato", 18);
        FOOD_DATABASE.put("Cucumber", 15);
        FOOD_DATABASE.put("Onion", 40);

        // Fruits (Healthy)
        FOOD_DATABASE.put("Apple", 52);
        FOOD_DATABASE.put("Banana", 89);
        FOOD_DATABASE.put("Berries", 57);
        FOOD_DATABASE.put("Orange", 47);
        FOOD_DATABASE.put("Grapes", 69);
        FOOD_DATABASE.put("Watermelon", 30);
        FOOD_DATABASE.put("Mango", 60);
        FOOD_DATABASE.put("Pineapple", 50);

        // Unhealthy/Junk Food
        FOOD_DATABASE.put("Pizza", 266);
        FOOD_DATABASE.put("Burger", 295);
        FOOD_DATABASE.put("French Fries", 312);
        FOOD_DATABASE.put("Soda", 41); // per 100ml
        FOOD_DATABASE.put("Chocolate", 546);
        FOOD_DATABASE.put("Ice Cream", 207);
        FOOD_DATABASE.put("Cake", 371);
        FOOD_DATABASE.put("Chips", 536);
    }

    // Helper class for food items with grams - MUST also be Serializable
    public static class FoodItem implements Serializable {
        private static final long serialVersionUID = 2L;

        private String foodName;
        private int grams;
        private int calories;

        public FoodItem() {
            // Default constructor for Serialization
            this.foodName = "";
            this.grams = 0;
            this.calories = 0;
        }

        public FoodItem(String foodName, int grams) {
            this.foodName = foodName;
            this.grams = grams;
            this.calories = calculateCalories(foodName, grams);
        }

        private int calculateCalories(String foodName, int grams) {
            Integer caloriesPer100g = FOOD_DATABASE.get(foodName);
            if (caloriesPer100g == null) {
                return 0; // Unknown food
            }
            return (caloriesPer100g * grams) / 100;
        }

        public String getFoodName() { return foodName; }
        public int getGrams() { return grams; }
        public int getCalories() { return calories; }

        @Override
        public String toString() {
            return grams + "g " + foodName + " (" + calories + " cal)";
        }
    }

    // Default constructor for Serialization
    public Meal() {
        this.mealName = "Default Meal";
        this.mealType = "Snack";
        this.foodItems = new HashMap<>();
        this.totalCalories = 0;
        this.isHealthy = true;
    }

    // Constructor for creating meal with just name and calories (backward compatibility)
    public Meal(String mealName, int calories, String mealType) {
        this.mealName = mealName;
        this.mealType = mealType;
        this.foodItems = new HashMap<>();
        this.totalCalories = calories;
        classifyMeal(); // Classify based on total calories
        this.isHealthy = (calories <= 800);
    }

    // Constructor for creating meal with food items
    public Meal(String mealName, String mealType) {
        this.mealName = mealName;
        this.mealType = mealType;
        this.foodItems = new HashMap<>();
        this.totalCalories = 0;
        this.isHealthy = true; // Default to healthy
    }

    // Add a food item to the meal
    public void addFoodItem(String foodName, int grams) {
        FoodItem item = new FoodItem(foodName, grams);
        foodItems.put(foodName, item);
        calculateTotalCalories();
        classifyMeal();
    }

    // Remove a food item
    public void removeFoodItem(String foodName) {
        foodItems.remove(foodName);
        calculateTotalCalories();
        classifyMeal();
    }

    // Calculate total calories from all food items
    private void calculateTotalCalories() {
        totalCalories = 0;
        for (FoodItem item : foodItems.values()) {
            totalCalories += item.getCalories();
        }
    }

    // Classify meal as healthy or not
    public void classifyMeal() {
        if (totalCalories > 800) {
            this.isHealthy = false;
            return;
        }

        // Additional logic: Check if contains unhealthy items
        for (String foodName : foodItems.keySet()) {
            if (isUnhealthyFood(foodName)) {
                this.isHealthy = false;
                return;
            }
        }

        this.isHealthy = true;
    }

    // Helper to check if food is generally unhealthy
    private boolean isUnhealthyFood(String foodName) {
        String[] unhealthyKeywords = {
                "pizza", "burger", "fries", "soda", "chocolate",
                "fried", "sugar", "ice cream", "cake", "chips",
                "donut", "candy", "cookie", "brownie", "pastry"
        };
        String lowerName = foodName.toLowerCase();
        for (String keyword : unhealthyKeywords) {
            if (lowerName.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Get nutritional summary
    public String getNutritionalSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Meal: ").append(mealName).append(" (").append(mealType).append(")\n");
        summary.append("Total Calories: ").append(totalCalories).append("\n");
        summary.append("Food Items:\n");

        for (FoodItem item : foodItems.values()) {
            summary.append("  - ").append(item.toString()).append("\n");
        }

        summary.append("Healthy: ").append(isHealthy ? "✅ Yes" : "❌ No");
        return summary.toString();
    }

    // Update meal
    public void updateMeal(String newMealName, String newMealType) {
        this.mealName = newMealName;
        this.mealType = newMealType;
        classifyMeal();
    }

    // Get number of food items in the meal
    public int getFoodItemCount() {
        return foodItems.size();
    }

    // Get list of all food names in this meal
    public String[] getFoodNames() {
        return foodItems.keySet().toArray(new String[0]);
    }

    // Getters
    public boolean isHealthy() { return isHealthy; }
    public String getMealName() { return mealName; }
    public String getMealType() { return mealType; }
    public int getTotalCalories() { return totalCalories; }
    public Map<String, FoodItem> getFoodItems() { return new HashMap<>(foodItems); }

    // Get all available food names from database
    public static String[] getAvailableFoods() {
        return FOOD_DATABASE.keySet().toArray(new String[0]);
    }

    @Override
    public String toString() {
        return mealName + " (" + mealType + ") - " + totalCalories + " cal - " +
                (isHealthy ? "Healthy" : "Unhealthy");
    }
}