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

import baritone.automation.core.AutomationController;
import baritone.automation.core.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class AutomationHudOverlay extends AbstractGui {
    private final Minecraft mc;
    private final AutomationController controller;
    private final FontRenderer font;
    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 12;

    public AutomationHudOverlay() {
        this.mc = Minecraft.getInstance();
        this.controller = AutomationController.getInstance();
        this.font = mc.fontRenderer;
    }

    public void render() {
        if (mc.gameSettings.showDebugInfo) return;

        PlayerEntity player = mc.player;
        if (player == null) return;

        int x = PADDING;
        int y = PADDING;

        // Zeige Hauptstatus
        String status = formatStatusLine();
        drawString(font, status, x, y, 0xFFFFFF);
        y += LINE_HEIGHT;

        // Zeige detaillierte Informationen wenn Automation aktiv
        if (controller.getCurrentState() != AutomationController.AutomationState.IDLE) {
            // Spieler-Stats
            String statsLine = formatStatsLine(player);
            drawString(font, statsLine, x, y, 0xAAAAAA);
            y += LINE_HEIGHT;

            // Aktuelle Action
            Action currentAction = controller.getCurrentAction();
            if (currentAction != null) {
                String actionLine = formatActionLine(currentAction);
                drawString(font, actionLine, x, y, 0xAAAAAA);
            }
        }
    }

    private String formatStatusLine() {
        StringBuilder sb = new StringBuilder();

        // Plan-Name und Status
        switch (controller.getCurrentState()) {
            case RUNNING:
                sb.append(TextFormatting.GREEN).append("▶ ");
                break;
            case PAUSED:
                sb.append(TextFormatting.YELLOW).append("❚❚ ");
                break;
            case BLOCKED:
                sb.append(TextFormatting.RED).append("⚠ ");
                break;
            default:
                sb.append(TextFormatting.GRAY).append("■ ");
        }

        // Füge Plan-Info hinzu
        sb.append(TextFormatting.AQUA)
          .append("[Auto] ")
          .append(TextFormatting.WHITE)
          .append(controller.getStatus());

        return sb.toString();
    }

    private String formatStatsLine(PlayerEntity player) {
        return String.format("%sHP: %d | Food: %d | XYZ: %d,%d,%d",
            TextFormatting.GRAY,
            (int)player.getHealth(),
            player.getFoodStats().getFoodLevel(),
            (int)player.getPosX(),
            (int)player.getPosY(),
            (int)player.getPosZ()
        );
    }

    private String formatActionLine(Action action) {
        return String.format("%s→ %s%s",
            TextFormatting.GRAY,
            TextFormatting.WHITE,
            action.getDescription()
        );
    }
}
