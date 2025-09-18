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

package baritone.automation.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.Minecraft;

/**
 * Adapter für Minecraft GUI-Komponenten
 */
public abstract class MinecraftScreenAdapter extends GuiScreen {
    protected final Minecraft mc;

    protected MinecraftScreenAdapter() {
        this.mc = Minecraft.getMinecraft();
    }

    protected void addButton(int x, int y, int width, int height, String text, Runnable onClick) {
        GuiButton button = new GuiButton(buttonList.size(), x, y, width, height, text);
        buttonList.add(button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Handle button clicks
    }

    protected void createTextField(int x, int y, int width, int height, String defaultText) {
        GuiTextField field = new GuiTextField(fontRenderer, x, y, width, height);
        field.setText(defaultText);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
