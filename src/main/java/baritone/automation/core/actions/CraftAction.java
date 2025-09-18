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
import baritone.automation.core.CraftingService;
import baritone.automation.util.AutomationLogger;
import baritone.automation.util.RetryHelper;

public class CraftAction implements Action {
    private final IBaritone baritone;
    private final CraftingService craftingService;
    private final AutomationLogger logger;
    private final String recipeId;
    private final int count;
    private boolean isDone = false;
    private int crafted = 0;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000;

    public CraftAction(IBaritone baritone, String recipeId, int count) {
        this.baritone = baritone;
        this.craftingService = new CraftingService();
        this.logger = AutomationLogger.getInstance();
        this.recipeId = recipeId;
        this.count = count;
    }

    @Override
    public boolean execute() {
        try {
            return RetryHelper.withRetry(
                () -> {
                    if (!craftingService.canCraft(recipeId, count - crafted)) {
                        logger.warning("Cannot craft " + recipeId + " - missing materials");
                        isDone = true;
                        return false;
                    }

                    logger.info("Starting crafting: " + recipeId + " (attempt to craft " + (count - crafted) + ")");
                    craftingService.startCrafting(recipeId, count - crafted);
                    return true;
                },
                success -> success,
                MAX_RETRIES,
                RETRY_DELAY,
                "craft_" + recipeId
            );
        } catch (Exception e) {
            logger.error("Failed to craft " + recipeId + ": " + e.getMessage());
            isDone = true;
            return false;
        }
    }

    @Override
    public boolean isDone() {
        if (isDone) return true;

        // Überprüfe Fortschritt und aktualisiere Status
        try {
            RetryHelper.waitUntil(
                unused -> !craftingService.isCraftingGuiOpen(),
                5000,
                100,
                "wait_for_crafting_complete"
            );

            // TODO: Implementiere Überprüfung der hergestellten Items
            isDone = true;
            return true;
        } catch (Exception e) {
            logger.warning("Crafting timeout for " + recipeId);
            return false;
        }
    }

    @Override
    public void cancel() {
        craftingService.closeCraftingTable();
        isDone = true;
    }

    @Override
    public String getDescription() {
        return String.format("Crafting %s (%d/%d)", recipeId, crafted, count);
    }

    @Override
    public ActionType getType() {
        return ActionType.CRAFT;
    }
}
