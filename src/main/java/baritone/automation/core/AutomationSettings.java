package baritone.automation.core;

public class AutomationSettings {
    private static AutomationSettings INSTANCE;

    private boolean woodCollectionEnabled = true;
    private boolean stoneAgeEnabled = true;
    private boolean ironTechEnabled = true;
    private boolean diamondHuntEnabled = true;
    private String lastPlan;

    private AutomationSettings() {}

    public static AutomationSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutomationSettings();
        }
        return INSTANCE;
    }

    public boolean isWoodCollectionEnabled() {
        return woodCollectionEnabled;
    }

    public void setWoodCollectionEnabled(boolean enabled) {
        this.woodCollectionEnabled = enabled;
    }

    public boolean isStoneAgeEnabled() {
        return stoneAgeEnabled;
    }

    public void setStoneAgeEnabled(boolean enabled) {
        this.stoneAgeEnabled = enabled;
    }

    public boolean isIronTechEnabled() {
        return ironTechEnabled;
    }

    public void setIronTechEnabled(boolean enabled) {
        this.ironTechEnabled = enabled;
    }

    public boolean isDiamondHuntEnabled() {
        return diamondHuntEnabled;
    }

    public void setDiamondHuntEnabled(boolean enabled) {
        this.diamondHuntEnabled = enabled;
    }

    public String getLastPlan() {
        return lastPlan;
    }

    public void setLastPlan(String plan) {
        this.lastPlan = plan;
    }
}
