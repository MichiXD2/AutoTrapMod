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
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.automation.core.actions.*;
import net.minecraft.util.math.BlockPos;

/**
 * Führt Actions aus und koordiniert die Interaktion mit Baritone APIs
 */
public class ActionExecutor {
    private final IBaritone baritone;
    private final InventoryService inventoryService;
    private final CraftingService craftingService;
    private Action currentAction;
    private int actionTimeout = 0;
    private static final int MAX_ACTION_TICKS = 2400; // 2 Minuten Timeout

    public ActionExecutor(IBaritone baritone) {
        this.baritone = baritone;
        this.inventoryService = new InventoryService();
        this.craftingService = new CraftingService();
    }

    public void tick() {
        if (currentAction == null) return;

        actionTimeout++;
        if (actionTimeout > MAX_ACTION_TICKS) {
            cancelCurrentAction();
            return;
        }

        if (currentAction.isDone()) {
            currentAction = null;
            actionTimeout = 0;
            return;
        }

        // Prüfe auf Stuck-Zustand und handle Fehler
        if (baritone.getPathingBehavior().isPathing() &&
            baritone.getPathingBehavior().ticksRemainingInSegment() > 100) {
            handleStuckState();
        }
    }

    public void executeAction(Action action) {
        if (currentAction != null) {
            cancelCurrentAction();
        }

        currentAction = action;
        actionTimeout = 0;
        action.execute();
    }

    public void cancelCurrentAction() {
        if (currentAction != null) {
            currentAction.cancel();
            currentAction = null;
        }
        baritone.getPathingBehavior().cancelEverything();
        actionTimeout = 0;
    }

    private void handleStuckState() {
        // Versuche alternative Pfade oder breche ab
        BlockPos playerPos = baritone.getPlayerContext().playerFeet();
        if (currentAction instanceof MineAction) {
            // Suche alternative Route zum Erz
            baritone.getCustomGoalProcess().setGoalAndPath(
                new GoalNear(playerPos, 30)
            );
        } else if (currentAction instanceof PathAction) {
            // Versuche einen anderen Y-Level
            baritone.getCustomGoalProcess().setGoalAndPath(
                new GoalYLevel(playerPos.getY() + 5)
            );
        }
    }

    public boolean hasActiveAction() {
        return currentAction != null;
    }

    public Action getCurrentAction() {
        return currentAction;
    }
}
