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

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import java.util.ArrayList;
import java.util.List;

public class TabPanel {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final List<Widget> widgets;
    private boolean visible;

    public TabPanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widgets = new ArrayList<>();
        this.visible = false;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        widgets.forEach(widget -> widget.visible = visible);
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public static class Builder {
        private final TabPanel panel;
        private int nextY;
        private static final int ELEMENT_HEIGHT = 20;
        private static final int PADDING = 4;

        public Builder(int x, int y, int width, int height) {
            this.panel = new TabPanel(x, y, width, height);
            this.nextY = y + PADDING;
        }

        public Builder addButton(String text, Button.IPressable onPress) {
            Button button = new Button(
                panel.x + PADDING,
                nextY,
                panel.width - PADDING * 2,
                ELEMENT_HEIGHT,
                new StringTextComponent(text),
                onPress
            );
            panel.addWidget(button);
            nextY += ELEMENT_HEIGHT + PADDING;
            return this;
        }

        public Builder addToggleButton(String text, boolean initialState, Button.IPressable onPress) {
            Button button = new Button(
                panel.x + PADDING,
                nextY,
                panel.width - PADDING * 2,
                ELEMENT_HEIGHT,
                new StringTextComponent(initialState ? "✓ " + text : "✗ " + text),
                onPress
            );
            panel.addWidget(button);
            nextY += ELEMENT_HEIGHT + PADDING;
            return this;
        }

        public TabPanel build() {
            return panel;
        }
    }
}
