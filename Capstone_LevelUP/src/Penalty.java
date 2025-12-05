import java.io.Serializable;


public class Penalty implements Serializable {

    private static final long serialVersionUID = 1L;

    private String penaltyId;
    private String penaltyType;
    private int magnitude; // XP amount to deduct
    private int durationHours;

    public Penalty(String id, String type, int magnitude, int duration) {
        this.penaltyId = id;
        this.penaltyType = type;
        this.magnitude = magnitude;
        this.durationHours = duration;
    }

    public void applyTo(User user) {
        System.out.println("Applying penalty: " + penaltyType + " (-" + magnitude + " XP)");
        // In a full implementation: user.deductXP(magnitude);
    }

    public void revoke(User user) {
        System.out.println("Penalty expired: " + penaltyType);
    }

    public String describe() {
        return penaltyType + ": -" + magnitude + " XP for " + durationHours + " hours.";
    }
    
    public int getMagnitude() { return magnitude; }
}
