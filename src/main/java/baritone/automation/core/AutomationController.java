/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.automation.core;

import baritone.api.IBaritone;
import baritone.api.Settings;

/**
 * Hauptcontroller für die Automation-Funktionalität.
 */
public class AutomationController {
    private static AutomationController INSTANCE;
    private final IBaritone baritone;
    private final ActionQueue actionQueue;
    private final FailsafeService failsafeService;
    private final InventoryService inventoryService;
    private final PlanLoader planLoader;
    private final AutomationLogger logger;
    private final AutomationProfiler profiler;
    private final AutomationSettings settings;
    private final BaritoneSettingsManager baritoneSettings;
    private AutomationState currentState;
    private AutomationPlan currentPlan;
    private long startTime;

    private AutomationController(IBaritone baritone) {
        this.baritone = baritone;
        this.actionQueue = new ActionQueue();
        this.failsafeService = new FailsafeService();
        this.inventoryService = new InventoryService();
        this.planLoader = new PlanLoader();
        this.logger = AutomationLogger.getInstance();
        this.profiler = AutomationProfiler.getInstance();
        this.settings = AutomationSettings.getInstance();
        this.baritoneSettings = new BaritoneSettingsManager(baritone);
        this.currentState = AutomationState.IDLE;
    }

    public static AutomationController getInstance(IBaritone baritone) {
        if (INSTANCE == null) {
            INSTANCE = new AutomationController(baritone);
        }
        return INSTANCE;
    }

    public void startPlan(String planName) {
        if (currentState != AutomationState.IDLE) {
            logger.warning("Cannot start plan while another plan is running");
            return;
        }

        try {
            currentPlan = planLoader.loadPlan(planName);
            if (currentPlan == null) {
                logger.error("Failed to load plan: " + planName, null);
                return;
            }

            // Konfiguriere Baritone
            baritoneSettings.applyMiningSettings();
            baritoneSettings.applySafetySettings();

            // Queue die Plan-Schritte
            actionQueue.queuePlan(currentPlan);

            currentState = AutomationState.RUNNING;
            startTime = System.currentTimeMillis();
            logger.info("Started plan: " + planName);
        } catch (Exception e) {
            logger.error("Error starting plan: " + planName, e);
            stop();
        }
    }

    public void tick() {
        if (currentState != AutomationState.RUNNING) {
            return;
        }

        profiler.startSection("automation_tick");

        try {
            failsafeService.checkSafety();
            baritoneSettings.updateDynamic();
            actionQueue.processTick();
            updateProgress();
        } catch (Exception e) {
            logger.error("Error during automation tick", e);
            pause();
        } finally {
            profiler.endSection();
        }
    }

    public void pause() {
        logger.warning("Pausing automation");
        if (currentPlan != null) {
            settings.setLastPlan(currentPlan.getName());
        }
        currentState = AutomationState.PAUSED;
        baritone.getPathingBehavior().cancelEverything();
    }

    public void resume() {
        if (currentState == AutomationState.PAUSED && currentPlan != null) {
            baritoneSettings.applyMiningSettings();
            baritoneSettings.applySafetySettings();
            actionQueue.queuePlan(currentPlan);
            currentState = AutomationState.RUNNING;
            startTime = System.currentTimeMillis();
        }
    }

    private boolean isStepEnabled(AutomationStep step) {
        String stepId = step.getId();
        if ("spawn_bootstrap".equals(stepId)) {
            return settings.isWoodCollectionEnabled();
        } else if ("stone_age".equals(stepId)) {
            return settings.isStoneAgeEnabled();
        } else if ("iron_tech".equals(stepId)) {
            return settings.isIronTechEnabled();
        } else if ("diamond_hunt".equals(stepId)) {
            return settings.isDiamondHuntEnabled();
        }
        return true;
    }

    private void updateProgress() {
        String currentAction = actionQueue.getCurrentAction() != null ?
            actionQueue.getCurrentAction().getDescription() : "No action";
        logger.info(String.format("Current action: %s", currentAction));
    }

    public void stop() {
        currentState = AutomationState.IDLE;
        baritone.getPathingBehavior().cancelEverything();
        if (currentPlan != null) {
            long runtime = System.currentTimeMillis() - startTime;
            logger.info(String.format("Automation stopped after %dms", runtime));
            baritoneSettings.restoreDefaultSettings();
        }
    }
}
