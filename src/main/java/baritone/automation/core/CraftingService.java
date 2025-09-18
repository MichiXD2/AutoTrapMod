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

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import java.util.Optional;

public class CraftingService {
    private final MinecraftClient mc;
    private final WorldInteractionHelper worldHelper;

    public CraftingService() {
        this.mc = MinecraftClient.getInstance();
        this.worldHelper = new WorldInteractionHelper();
    }

    public boolean canCraft(String recipeId, int count) {
        if (mc.world == null) return false;

        Optional<? extends Recipe<?>> recipe = mc.world.getRecipeManager()
            .getRecipes()
            .stream()
            .filter(r -> r.getType() == RecipeType.CRAFTING)
            .filter(r -> r instanceof CraftingRecipe)
            .filter(r -> r.toString().equals(recipeId))
            .findFirst();

        return recipe.isPresent() && hasRequiredMaterials((CraftingRecipe)recipe.get(), count);
    }

    public void queueCraftingAction(String recipeId, int count) {
        if (!canCraft(recipeId, count)) {
            return;
        }

        if (!isCraftingGuiOpen()) {
            openCraftingTable();
        }

        // TODO: Implementiere das Platzieren der Items und den Craft-Vorgang
    }

    private boolean hasRequiredMaterials(CraftingRecipe recipe, int count) {
        // TODO: Implementiere die Prüfung der benötigten Materialien
        return false;
    }

    public void openCraftingTable() {
        if (mc.world == null || mc.player == null) return;

        // Suche nach einem Werkbank-Block in der Nähe
        BlockPos craftingTablePos = worldHelper.findNearbyBlock(net.minecraft.block.Blocks.CRAFTING_TABLE, 5);

        if (craftingTablePos != null) {
            worldHelper.interactWithBlock(craftingTablePos);
        }
    }

    public void closeCraftingTable() {
        if (mc.player != null) {
            mc.player.closeScreen();
        }
    }

    public boolean isCraftingGuiOpen() {
        return mc.player != null &&
               mc.currentScreen instanceof CraftingScreenHandler;
    }
}
