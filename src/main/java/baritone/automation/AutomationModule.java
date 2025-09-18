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

package baritone.automation;

import baritone.automation.core.AutomationController;
import baritone.automation.gui.AutomationScreen;
import baritone.automation.gui.AutomationHudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class AutomationModule {
    private static final String CATEGORY = "key.categories.baritone.automation";
    private static final KeyBinding TOGGLE_KEY = new KeyBinding(
        "key.baritone.automation.toggle",
        InputMappings.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        CATEGORY
    );

    private final AutomationController controller;
    private final AutomationHudOverlay hudOverlay;

    public AutomationModule() {
        this.controller = AutomationController.getInstance();
        this.hudOverlay = new AutomationHudOverlay();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            controller.tick();
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            hudOverlay.render();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (TOGGLE_KEY.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new AutomationScreen());
        }
    }
}
