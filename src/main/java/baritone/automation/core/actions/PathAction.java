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
import baritone.api.pathing.goals.*;
import baritone.automation.core.Action;
import net.minecraft.util.math.BlockPos;

public class PathAction implements Action {
    private final IBaritone baritone;
    private final String goalType;
    private final int targetY;
    private final int area;
    private boolean isDone = false;

    public PathAction(IBaritone baritone, String goalType, int targetY, int area) {
        this.baritone = baritone;
        this.goalType = goalType;
        this.targetY = targetY;
        this.area = area;
    }

    @Override
    public boolean execute() {
        Goal goal;
        BlockPos playerPos = baritone.getPlayerContext().playerFeet();

        switch (goalType) {
            case "yLevelBox":
                goal = new GoalXZ(playerPos.getX() + area/2, playerPos.getZ() + area/2);
                break;
            case "yLevel":
                goal = new GoalYLevel(targetY);
                break;
            case "near":
                goal = new GoalNear(playerPos, area);
                break;
            default:
                goal = new GoalXZ(playerPos.getX(), playerPos.getZ());
        }

        baritone.getCustomGoalProcess().setGoalAndPath(goal);
        return true;
    }

    @Override
    public boolean isDone() {
        if (isDone) return true;

        // Prüfe ob das Ziel erreicht wurde
        if (!baritone.getPathingBehavior().isPathing()) {
            BlockPos pos = baritone.getPlayerContext().playerFeet();
            if (goalType.equals("yLevel") || goalType.equals("yLevelBox")) {
                isDone = Math.abs(pos.getY() - targetY) <= 2;
            } else {
                isDone = true; // Bei anderen Zielen nehmen wir an, dass der Pathfinder uns korrekt geführt hat
            }
        }

        return isDone;
    }

    @Override
    public void cancel() {
        baritone.getPathingBehavior().cancelEverything();
        isDone = true;
    }

    @Override
    public String getDescription() {
        return String.format("Pathing to %s (y=%d, area=%d)", goalType, targetY, area);
    }

    @Override
    public ActionType getType() {
        return ActionType.PATH;
    }
}
