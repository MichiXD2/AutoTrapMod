package baritone.automation.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class AutomationPlan {
    private final String name;
    private final String version;
    private final List<AutomationStep> steps;
    private final Map<String, Object> failsafes;

    public AutomationPlan(String name, String version, List<AutomationStep> steps, Map<String, Object> failsafes) {
        this.name = name;
        this.version = version;
        this.steps = new ArrayList<>(steps);
        this.failsafes = failsafes;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<AutomationStep> getSteps() {
        return new ArrayList<>(steps);
    }

    public Map<String, Object> getFailsafes() {
        return failsafes;
    }
}
