public class Penalty {
    // Attributes
    private String penaltyId;
    private String penaltyType; // "XP_REDUCTION", "STREAK_FREEZE"
    private int magnitude; // e.g., -50 XP
    private int durationHours;

    // Methods from Diagram
    public void applyTo(User user) {
        // TODO: Execute the penalty logic on user
    }

    public void revoke(User user) {
        // TODO: Remove effect after duration expires
    }

    public String describe() {
        return "Penalty: " + penaltyType + " for " + durationHours + " hours.";
    }
}