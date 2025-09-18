package baritone.automation.core;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private Queue<AutomationAction> actions;
    private AutomationAction currentAction;

    public ActionQueue() {
        this.actions = new LinkedList<>();
    }

    public void queuePlan(AutomationPlan plan) {
        // Konvertiere Plan-Schritte in Aktionen
        for (AutomationStep step : plan.getSteps()) {
            actions.offer(new AutomationAction(step));
        }
    }

    public void processTick() {
        if (currentAction == null && !actions.isEmpty()) {
            currentAction = actions.poll();
            currentAction.start();
        }

        if (currentAction != null) {
            if (currentAction.isComplete()) {
                currentAction = null;
            } else {
                currentAction.tick();
            }
        }
    }

    public AutomationAction getCurrentAction() {
        return currentAction;
    }

    public void clear() {
        actions.clear();
        currentAction = null;
    }
}
