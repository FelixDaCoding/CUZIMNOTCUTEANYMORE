import java.util.Scanner;
import java.time.LocalDate;
import java.time.DayOfWeek;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("========== SYSTEM START: LevelUP ==========");

        // 1. AUTHENTICATION PHASE
        while (currentUser == null) {
            System.out.println("\n--- WELCOME ---");
            System.out.println("1. Login");
            System.out.println("2. Register New Account");
            System.out.print("Select: ");
            String authChoice = scanner.nextLine();

            if (authChoice.equals("1")) {
                handleLogin();
            } else if (authChoice.equals("2")) {
                handleRegistration();
            } else {
                System.out.println("Invalid option.");
            }
        }

        System.out.println("\n>> Welcome, " + currentUser.getName());

        // 2. ASSIGN DYNAMIC DAILY CHALLENGE
        initializeDailyChallenge();

        // 3. MAIN MENU LOOP
        boolean running = true;
        while (running) {
            printStats(currentUser);
            System.out.println("\n--- ACTION MENU ---");
            System.out.println("1. Log Workout (Adapts to Type)");
            System.out.println("2. Log Meal");
            System.out.println("3. Check Daily Challenge");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogWorkout();
                    break;
                case "2":
                    handleLogMeal();
                    break;
                case "3":
                    handleDailyChallenge();
                    break;
                case "4":
                    running = false;
                    System.out.println(">> Exiting System. Keep grinding!");
                    break;
                default:
                    System.out.println(">> Invalid option.");
            }
        }
    }

    // --- Auth Handlers ---

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = User.authenticate(username, password);

        if (currentUser == null) {
            System.out.println(">> Invalid credentials.");
        }
    }

    private static void handleRegistration() {
        System.out.println("\n--- NEW ACCOUNT ---");
        System.out.print("Enter Display Name: ");
        String name = scanner.nextLine();

        System.out.print("Choose Username: ");
        String username = scanner.nextLine();

        System.out.print("Choose Password: ");
        String password = scanner.nextLine();

        User newUser = User.register(name, username, password);
        if (newUser != null) {
            System.out.println(">> Account created successfully! Logging you in...");
            currentUser = newUser;
        } else {
            System.out.println(">> Error: Username '" + username + "' is already taken.");
        }
    }

    // --- System Logic ---

    private static void initializeDailyChallenge() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        DailyChallenge daily = null;

        System.out.println("\n>> Generating Challenge for: " + today);

        switch (today) {
            case MONDAY: daily = new DailyChallenge("Marathon Monday", "Run for 20 mins", 20, "Run_Duration"); break;
            case TUESDAY: daily = new DailyChallenge("Tank Tuesday", "Do 50 Pushups", 50, "Pushups_Reps"); break;
            case WEDNESDAY: daily = new DailyChallenge("Wheels Wednesday", "Cycle for 30 mins", 30, "Cycling_Duration"); break;
            case THURSDAY: daily = new DailyChallenge("Thunder Thursday", "Do 40 Squats", 40, "Squats_Reps"); break;
            case FRIDAY: daily = new DailyChallenge("Flex Friday", "Do 30 Curls", 30, "Curls_Reps"); break;
            case SATURDAY: daily = new DailyChallenge("Stamina Saturday", "Burn 500 Calories", 500, "Calories"); break;
            case SUNDAY: daily = new DailyChallenge("Sunday Stretch", "Yoga for 15 mins", 15, "Yoga_Duration"); break;
        }

        currentUser.setDailyChallenge(daily);
        System.out.println(">> NEW QUEST ASSIGNED: " + daily.getChallengeName());
    }

    private static void handleLogWorkout() {
        System.out.println("\n-- Log Workout --");
        System.out.println("1. Running");
        System.out.println("2. Cycling");
        System.out.println("3. Pushups");
        System.out.println("4. Squats");
        System.out.println("5. Curls");
        System.out.println("6. Yoga");
        System.out.print("Select Type: ");

        String choice = scanner.nextLine();
        String name = "Custom";
        boolean isCardio = true;

        switch (choice) {
            case "1": name = "Running"; isCardio = true; break;
            case "2": name = "Cycling"; isCardio = true; break;
            case "3": name = "Pushups"; isCardio = false; break;
            case "4": name = "Squats"; isCardio = false; break;
            case "5": name = "Curls"; isCardio = false; break;
            case "6": name = "Yoga"; isCardio = true; break;
            default: System.out.println("Invalid type, defaulting to Cardio.");
        }

        Workout w;
        System.out.print("Intensity (Low, Medium, High): ");
        String intensity = scanner.nextLine();

        if (isCardio) {
            System.out.print("Duration (mins): ");
            int duration = Integer.parseInt(scanner.nextLine());
            w = new Workout(name, duration, intensity);
        } else {
            System.out.print("Reps per Set: ");
            int reps = Integer.parseInt(scanner.nextLine());
            System.out.print("Total Sets: ");
            int sets = Integer.parseInt(scanner.nextLine());
            w = new Workout(name, reps, sets, intensity);
        }

        int xp = currentUser.logWorkout(w);
        System.out.println(">> Success! Burned " + w.getCaloriesBurned() + " kcal | XP +" + xp);

        // Updated check function
        checkChallengeCompletion(w);
    }

    private static void checkChallengeCompletion(Workout w) {
        DailyChallenge dc = currentUser.getDailyChallenge();
        if (dc.isCompleted()) return;

        String type = dc.getTargetType();   // e.g. "Run_Duration" or "Calories"

        // 1. Check Calorie Challenge
        if (type.equalsIgnoreCase("Calories")) {
            // Add calories to the counter
            dc.addToProgress(w.getCaloriesBurned(), currentUser);
        }
        // 2. Check Activity Specific Challenge
        else {
            String[] parts = type.split("_");
            if (parts.length < 2) return;

            String requiredActivity = parts[0].toLowerCase(); // "run"

            // Check if Workout Name matches (e.g. "Running" contains "run")
            if (w.getWorkoutName().toLowerCase().contains(requiredActivity)) {

                double valueAchieved = 0;

                if (w.getType().equals("Cardio")) {
                    valueAchieved = w.getDurationMins();
                } else {
                    valueAchieved = w.getReps() * w.getSets(); // Total Reps
                }

                // Add stats to the counter
                dc.addToProgress(valueAchieved, currentUser);
            }
        }
    }

    private static void handleLogMeal() {
        System.out.println("\n-- Log Meal --");
        System.out.print("Meal Name: ");
        String name = scanner.nextLine();
        System.out.print("Calories: ");
        int calories = Integer.parseInt(scanner.nextLine());
        System.out.print("Type: ");
        String type = scanner.nextLine();

        Meal m = new Meal(name, calories, type);
        int xp = currentUser.logMeal(m);

        if (m.isHealthy()) System.out.println(">> Healthy Meal! +" + xp + " XP");
        else System.out.println(">> Meal Logged. No XP (Unhealthy).");
    }

    private static void handleDailyChallenge() {
        DailyChallenge dc = currentUser.getDailyChallenge();
        System.out.println("\n-- Daily Challenge --");
        System.out.println("Goal: " + dc.getChallengeName());
        System.out.println("Desc: " + dc.getDescription());
        System.out.println("Status: " + (dc.isCompleted() ? "COMPLETED" : "INCOMPLETE"));
        System.out.println("Progress: " + (int)dc.getCurrentProgress() + " / " + (int)dc.getTargetValue());

        if (!dc.isCompleted()) {
            System.out.print("Force complete? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                dc.markComplete(currentUser);
                currentUser.getStreak().updateStreak(currentUser);
                System.out.println(">> Force Completed.");
            }
        }
    }

    private static void printStats(User u) {
        System.out.println("\n[STATUS] " + u.getName() + " | Lvl: " + u.getLevel() + " | XP: " + u.getXp());
        System.out.println("         Streak: " + u.getStreak().getCurrentStreak() + " Days");
    }
}