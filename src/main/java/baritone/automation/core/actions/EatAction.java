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
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EatAction implements Action {
    private final IBaritone baritone;
    private final InventoryService inventoryService;
    private boolean isDone = false;
    private int ticksEating = 0;
    private static final int EATING_DURATION = 32; // Standard-Essdauer in Ticks

    public EatAction(IBaritone baritone) {
        this.baritone = baritone;
        this.inventoryService = new InventoryService();
    }

    @Override
    public boolean execute() {
        // Finde essbares Item im Inventar
        if (!findAndSelectFood()) {
            isDone = true;
            return false;
        }

        // Starte Ess-Vorgang
        baritone.getPlayerController().processRightClick();
        return true;
    }

    private boolean findAndSelectFood() {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = baritone.getPlayerContext().player().inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem().isFood()) {
                baritone.getPlayerContext().player().inventory.currentItem = slot;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDone() {
        if (isDone) return true;

        // Überprüfe ob der Spieler voll gesättigt ist
        if (baritone.getPlayerContext().player().getFoodStats().getFoodLevel() >= 20) {
            isDone = true;
            return true;
        }

        // Überprüfe Ess-Fortschritt
        if (baritone.getPlayerContext().player().isHandActive()) {
            ticksEating++;
            if (ticksEating >= EATING_DURATION) {
                isDone = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public void cancel() {
        if (baritone.getPlayerContext().player().isHandActive()) {
            baritone.getPlayerContext().player().stopActiveHand();
        }
        isDone = true;
    }

    @Override
    public String getDescription() {
        return "Eating food to restore hunger";
    }

    @Override
    public ActionType getType() {
        return ActionType.EAT;
    }
}
