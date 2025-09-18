package baritone.automation.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanLoader {
    private static final String PLANS_DIRECTORY = "config/automation/";
    private final Gson gson;
    private final AutomationLogger logger;

    public PlanLoader() {
        this.gson = new GsonBuilder().create();
        this.logger = AutomationLogger.getInstance();
    }

    public AutomationPlan loadPlan(String planName) {
        if (!planName.endsWith(".plan.json")) {
            planName += ".plan.json";
        }

        File planFile = new File(PLANS_DIRECTORY + planName);
        if (!planFile.exists()) {
            logger.error("Plan file not found: " + planFile.getAbsolutePath(), null);
            return null;
        }

        try (FileReader reader = new FileReader(planFile)) {
            PlanData planData = gson.fromJson(reader, PlanData.class);
            List<AutomationStep> steps = new ArrayList<>();

            for (Map<String, String> stepData : planData.steps) {
                steps.add(new AutomationStep(
                    stepData.get("id"),
                    stepData.get("description")
                ));
            }

            return new AutomationPlan(
                planData.name,
                planData.version,
                steps,
                planData.failsafes != null ? planData.failsafes : new HashMap<>()
            );
        } catch (IOException e) {
            logger.error("Error loading plan: " + planName, e);
            return null;
        }
    }

    private static class PlanData {
        String name;
        String version;
        List<Map<String, String>> steps;
        Map<String, Object> failsafes;
    }
}
