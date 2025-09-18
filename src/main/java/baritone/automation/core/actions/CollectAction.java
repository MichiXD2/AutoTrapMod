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

package baritone.automation.core.actions;

import baritone.api.IBaritone;
import baritone.automation.core.Action;
import baritone.automation.core.InventoryService;
import baritone.automation.util.AutomationLogger;
import baritone.api.pathing.goals.GoalXZ;
import net.minecraft.util.math.BlockPos;

public class CollectAction implements Action {
    private final IBaritone baritone;
    private final InventoryService inventoryService;
    private final AutomationLogger logger;
    private final String itemId;
    private final int count;
    private final int searchRadius;
    private final String strategy;
    private boolean isDone = false;
    private int collected = 0;
    private long lastProgressTime;

    public CollectAction(IBaritone baritone, String itemId, int count, int searchRadius, String strategy) {
        this.baritone = baritone;
        this.inventoryService = new InventoryService();
        this.logger = AutomationLogger.getInstance();
        this.itemId = itemId;
        this.count = count;
        this.searchRadius = searchRadius;
        this.strategy = strategy;
        this.lastProgressTime = System.currentTimeMillis();
    }

    @Override
    public boolean execute() {
        // Initialer Item-Count
        collected = inventoryService.getItemCount(itemId);
        logger.info(String.format("Starting collection of %s (%d/%d)", itemId, collected, count));

        // Wähle Sammelstrategie
        switch (strategy) {
            case "huntOrHarvest":
                return executeHuntOrHarvestStrategy();
            case "mine":
                return executeMineStrategy();
            default:
                return executeDefaultStrategy();
        }
    }

    private boolean executeHuntOrHarvestStrategy() {
        // Spezielle Strategie für Nahrungssuche
        if (itemId.equals("food_any")) {
            // Suche nach Tieren oder Pflanzen in der Nähe
            BlockPos playerPos = baritone.getPlayerContext().playerFeet();
            baritone.getCustomGoalProcess().setGoalAndPath(
                new GoalXZ(playerPos.getX() + searchRadius/2, playerPos.getZ() + searchRadius/2)
            );
        }
        return true;
    }

    private boolean executeMineStrategy() {
        // Nutze Baritone's Mine-Process
        baritone.getMineProcess().mine(count - collected, itemId);
        return true;
    }

    private boolean executeDefaultStrategy() {
        // Standard-Sammelstrategie
        BlockPos playerPos = baritone.getPlayerContext().playerFeet();
        baritone.getCustomGoalProcess().setGoalAndPath(
            new GoalXZ(playerPos.getX() + searchRadius, playerPos.getZ() + searchRadius)
        );
        return true;
    }

    @Override
    public boolean isDone() {
        if (isDone) return true;

        // Prüfe aktuellen Fortschritt
        int currentCount = inventoryService.getItemCount(itemId);
        if (currentCount > collected) {
            collected = currentCount;
            lastProgressTime = System.currentTimeMillis();
            logger.info(String.format("Collected %s: %d/%d", itemId, collected, count));
        }

        // Prüfe ob Ziel erreicht
        if (collected >= count) {
            isDone = true;
            logger.success(String.format("Collection complete: %s (%d/%d)", itemId, collected, count));
            return true;
        }

        // Timeout nach 5 Minuten ohne Fortschritt
        if (System.currentTimeMillis() - lastProgressTime > 300000) {
            logger.warning(String.format("Collection timeout for %s after 5 minutes without progress", itemId));
            isDone = true;
            return true;
        }

        return false;
    }

    @Override
    public void cancel() {
        baritone.getPathingBehavior().cancelEverything();
        baritone.getMineProcess().cancel();
        isDone = true;
    }

    @Override
    public String getDescription() {
        return String.format("Collecting %s (%d/%d)", itemId, collected, count);
    }

    @Override
    public ActionType getType() {
        return ActionType.COLLECT;
    }
}
