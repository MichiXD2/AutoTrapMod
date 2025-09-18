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
import baritone.automation.util.RetryHelper;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SmeltAction implements Action {
    private final IBaritone baritone;
    private final InventoryService inventoryService;
    private final AutomationLogger logger;
    private final String input;
    private final String fuel;
    private final int batchSize;
    private boolean isDone = false;
    private BlockPos furnacePos;

    public SmeltAction(IBaritone baritone, String input, String fuel, int batchSize) {
        this.baritone = baritone;
        this.inventoryService = new InventoryService();
        this.logger = AutomationLogger.getInstance();
        this.input = input;
        this.fuel = fuel;
        this.batchSize = batchSize;
    }

    @Override
    public boolean execute() {
        try {
            return RetryHelper.withRetry(
                () -> {
                    // Suche/platziere Ofen
                    if (!findOrPlaceFurnace()) {
                        logger.error("Could not find or place furnace");
                        return false;
                    }

                    // Öffne Ofen-GUI
                    if (!openFurnace()) {
                        logger.error("Could not open furnace");
                        return false;
                    }

                    // Platziere Items
                    FurnaceContainer container = (FurnaceContainer) baritone.getPlayerContext().player().openContainer;
                    if (container == null) return false;

                    // Platziere Input
                    if (!placeItemInFurnace(input, 0)) return false; // Slot 0 = Input

                    // Platziere Fuel
                    if (!placeItemInFurnace(fuel, 1)) return false; // Slot 1 = Fuel

                    return true;
                },
                success -> success,
                3,
                1000,
                "smelt_" + input
            );
        } catch (Exception e) {
            logger.error("Failed to start smelting: " + e.getMessage());
            isDone = true;
            return false;
        }
    }

    private boolean findOrPlaceFurnace() {
        // TODO: Implementiere Ofen-Suche/Platzierung
        return false;
    }

    private boolean openFurnace() {
        if (furnacePos == null) return false;
        // TODO: Implementiere Ofen-GUI-Öffnung
        return false;
    }

    private boolean placeItemInFurnace(String itemId, int slot) {
        if (!inventoryService.hasItem(itemId)) {
            logger.warning("Missing " + itemId + " for smelting");
            return false;
        }

        // TODO: Implementiere Item-Platzierung im Ofen
        return true;
    }

    @Override
    public boolean isDone() {
        if (isDone) return true;

        try {
            FurnaceContainer container = (FurnaceContainer) baritone.getPlayerContext().player().openContainer;
            if (container == null) {
                return false;
            }

            // Prüfe ob Schmelzvorgang abgeschlossen ist
            if (container.getFurnaceBurnTime() == 0 &&
                container.getCookTime() == 0) {
                collectOutput();
                isDone = true;
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.warning("Error checking furnace progress: " + e.getMessage());
            return false;
        }
    }

    private void collectOutput() {
        // TODO: Implementiere Output-Sammlung
    }

    @Override
    public void cancel() {
        if (baritone.getPlayerContext().player().openContainer instanceof FurnaceContainer) {
            baritone.getPlayerContext().player().closeScreen();
        }
        isDone = true;
    }

    @Override
    public String getDescription() {
        return String.format("Smelting %s using %s", input, fuel);
    }

    @Override
    public ActionType getType() {
        return ActionType.SMELT;
    }
}
