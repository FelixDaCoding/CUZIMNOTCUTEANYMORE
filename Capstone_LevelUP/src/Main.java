import java.util.Scanner;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Map;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("========== SYSTEM START: LevelUP ==========");
        boolean systemRunning = true;

        while (systemRunning) {
            currentUser = null;

            // 1. AUTHENTICATION PHASE
            while (currentUser == null && systemRunning) {
                System.out.println("\n--- WELCOME ---");
                System.out.println("1. Login");
                System.out.println("2. Register New Account");
                System.out.println("3. Exit System");
                System.out.print("Select: ");
                String authChoice = scanner.nextLine();

                if (authChoice.equals("1")) handleLogin();
                else if (authChoice.equals("2")) handleRegistration();
                else if (authChoice.equals("3")) systemRunning = false;
                else System.out.println("Invalid option.");
            }

            if (!systemRunning) break;

            System.out.println("\n>> Welcome, " + currentUser.getName() + " (ID: " + currentUser.getUserId() + ")");

            // 2. ASSIGN DYNAMIC DAILY CHALLENGE
            if (currentUser.getDailyChallenge() == null) {
                initializeDailyChallenge();
            }

            // 3. SESSION LOOP
            boolean sessionActive = true;
            while (sessionActive) {
                printDashboard();
                System.out.println("\n--- ACTION MENU ---");
                System.out.println("1. Log Workout");
                System.out.println("2. Log Meal");
                System.out.println("3. View Challenge Page");
                System.out.println("4. Logout");
                System.out.print("Choose: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1": handleLogWorkout(); break;
                    case "2": handleLogMeal(); break;
                    case "3": handleDailyChallengeUI(); break;
                    case "4": sessionActive = false; break;
                    default: System.out.println("Invalid option.");
                }
            }
        }
        System.out.println("========== SYSTEM END ==========");
    }

    // --- UI Simulation Methods ---

    private static void printDashboard() {
        System.out.println("\n[HOME DASHBOARD]");
        System.out.println("Hi, " + currentUser.getName() + "!");

        int[] weeklyStats = currentUser.getWeeklyWorkoutMinutes();
        int totalMins = currentUser.getTotalWeeklyMinutes();

        System.out.print("Weekly Graph (Mins): [");
        for (int min : weeklyStats) System.out.print(min + " ");
        System.out.println("]");
        System.out.println("Total Weekly Minutes: " + totalMins);
        System.out.println("Total XP: " + currentUser.getXp() + " | Level: " + currentUser.getLevel());
    }

    private static void handleDailyChallengeUI() {
        DailyChallenge dc = currentUser.getDailyChallenge();
        System.out.println("\n[CHALLENGE PAGE]");
        System.out.println("Title: " + dc.getChallengeName());

        System.out.print("Difficulty: ");
        for(int i=0; i<dc.getDifficulty(); i++) System.out.print("★");
        System.out.println();

        // NEW: Print Activity Breakdown from the Challenge Object
        System.out.println("\nActivity Breakdown (Target):");
        for (ChallengeTask task : dc.getTasks()) {
            String check = task.isComplete() ? "[x]" : "[ ]";
            System.out.println(check + " " + task.getTaskName() + ": " + task.getStatus());
        }

        System.out.println("\nRewards: Streak +" + dc.getStreakBonus() + " | XP +" + dc.getXpReward());
    }

    // --- Logic Handlers ---

    private static void initializeDailyChallenge() {
        // Create a Composite Challenge matching the Screenshot
        DailyChallenge daily = new DailyChallenge("Burn 240 Calories Today", "Complete all activities", 4, 130, 10);

        // Add Sub-Tasks
        daily.addTask("Calories", 240, "Kcal");
        daily.addTask("Pushups", 15, "Reps");
        daily.addTask("Crunches", 15, "Reps");
        daily.addTask("Squats", 25, "Reps");
        daily.addTask("Jog", 20, "Mins"); // Mapped "Jog" to Duration for simulation logic

        currentUser.setDailyChallenge(daily);
        System.out.println(">> NEW QUEST ASSIGNED: " + daily.getChallengeName());
    }

    private static void handleLogWorkout() {
        System.out.println("\n-- Log Workout --");
        System.out.println("1. Jog (Cardio)");
        System.out.println("2. Pushups (Strength)");
        System.out.println("3. Crunches (Strength)");
        System.out.println("4. Squats (Strength)");
        System.out.println("5. Custom");
        System.out.print("Select Type: ");

        String choice = scanner.nextLine();
        String name = "Custom";
        boolean isCardio = true;

        switch (choice) {
            case "1": name = "Jog"; isCardio = true; break;
            case "2": name = "Pushups"; isCardio = false; break;
            case "3": name = "Crunches"; isCardio = false; break;
            case "4": name = "Squats"; isCardio = false; break;
            case "5":
                System.out.print("Enter Name: ");
                name = scanner.nextLine();
                System.out.print("Is Cardio (y/n)? ");
                isCardio = scanner.nextLine().equalsIgnoreCase("y");
                break;
        }

        Workout w;
        System.out.print("Intensity (Low, Medium, High): ");
        String intensity = scanner.nextLine();

        if (isCardio) {
            System.out.print("Enter Duration (minutes): ");
            int duration = Integer.parseInt(scanner.nextLine());
            w = new Workout(name, duration, intensity);
        } else {
            System.out.print("Enter Reps per Set: ");
            int reps = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Total Sets: ");
            int sets = Integer.parseInt(scanner.nextLine());
            w = new Workout(name, reps, sets, intensity);
        }

        int xp = currentUser.logWorkout(w);
        System.out.println(">> Success! Burned " + (int)w.getCaloriesBurned() + " kcal | XP +" + xp);

        // Smart Update: The Challenge object figures out what to update
        currentUser.getDailyChallenge().processWorkout(w, currentUser);
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

    private static void handleLogin() {
        System.out.print("User: ");
        String u = scanner.nextLine();
        System.out.print("Pass: ");
        String p = scanner.nextLine();
        currentUser = User.authenticate(u, p);
        if(currentUser == null) System.out.println("Invalid.");
    }

    private static void handleRegistration() {
        System.out.print("Name: ");
        String n = scanner.nextLine();
        System.out.print("User: ");
        String u = scanner.nextLine();
        System.out.print("Pass: ");
        String p = scanner.nextLine();
        User newUser = User.register(n, u, p);
        if(newUser != null) {
            System.out.println("Account Created! Please Log In.");
        } else {
            System.out.println("Username Taken.");
        }
    }
}