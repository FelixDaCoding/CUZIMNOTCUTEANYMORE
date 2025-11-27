public class Reward {
    // Attributes
    private String rewardId;
    private String rewardName;
    private String requirement; // e.g., "Level 5"
    private String rewardType; // "Badge", "Title", "Item"
    private boolean isUnlocked;

    // Methods from Diagram
    public void unlock(User user) {
        // TODO: Set isUnlocked = true
    }

    public void assignToUser(User user) {
        // TODO: user.addReward(this)
    }

    public void displayReward() {
        // TODO: Show UI popup
    }
}