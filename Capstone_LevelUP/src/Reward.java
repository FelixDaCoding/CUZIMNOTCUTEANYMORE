public class Reward {
    private String rewardId;
    private String rewardName;
    private int levelRequirement; // Changed string to int for easier logic
    private String rewardType;
    private boolean isUnlocked;

    public Reward(String name, int levelRequirement, String type) {
        this.rewardName = name;
        this.levelRequirement = levelRequirement;
        this.rewardType = type;
        this.isUnlocked = false;
    }

    public void unlock(User user) {
        if (user.getLevel() >= this.levelRequirement) {
            this.isUnlocked = true;
            System.out.println("Reward Unlocked: " + rewardName);
        }
    }

    public void assignToUser(User user) {
        if (isUnlocked) {
            user.addReward(this);
        }
    }

    public String displayReward() {
        return isUnlocked ? rewardName + " (" + rewardType + ")" : "Locked Reward";
    }
    
    public String getRewardName() { return rewardName; }
}
