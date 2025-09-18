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
import baritone.automation.core.AutomationSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class AutomationScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 4;

    private final AutomationSettings settings;
    private final AutomationController controller;

    private Button startButton;
    private Button stopButton;

    private enum Tab {
        OVERVIEW,
        PLAN,
        SETTINGS
    }

    private Tab currentTab = Tab.OVERVIEW;

    public AutomationScreen() {
        super(new StringTextComponent("Automation"));
        this.settings = AutomationSettings.getInstance();
        this.controller = AutomationController.getInstance(null); // TODO: Get IBaritone instance
    }

    @Override
    protected void init() {
        int buttonWidth = 98;
        int centerX = this.width / 2;

        // Tab buttons
        this.addButton(new Button(10, 10, buttonWidth, BUTTON_HEIGHT,
            "Overview", button -> switchTab(Tab.OVERVIEW)));
        this.addButton(new Button(10 + buttonWidth + PADDING, 10, buttonWidth, BUTTON_HEIGHT,
            "Plan", button -> switchTab(Tab.PLAN)));
        this.addButton(new Button(10 + (buttonWidth + PADDING) * 2, 10, buttonWidth, BUTTON_HEIGHT,
            "Settings", button -> switchTab(Tab.SETTINGS)));

        // Control buttons
        startButton = this.addButton(new Button(centerX - 100, this.height - 30,
            95, BUTTON_HEIGHT, settings.getLastPlan() != null ? "Start " + settings.getLastPlan() : "Start",
            button -> onStartPressed()));
        stopButton = this.addButton(new Button(centerX + 5, this.height - 30,
            95, BUTTON_HEIGHT, "Stop", button -> onStopPressed()));

        initializeTabContent();
    }

    private void initializeTabContent() {
        int y = 40 + BUTTON_HEIGHT + PADDING;
        int buttonWidth = 200;
        int x = (this.width - buttonWidth) / 2;

        if (currentTab == Tab.SETTINGS) {
            addSettingsButtons(x, y, buttonWidth);
        }
    }

    private void addSettingsButtons(int x, int y, int buttonWidth) {
        // Wood Collection Toggle
        this.addButton(new Button(x, y, buttonWidth, BUTTON_HEIGHT,
            getToggleButtonText("Enable Wood Collection", settings.isWoodCollectionEnabled()),
            button -> onWoodCollectionToggle(button)));

        // Stone Age Toggle
        this.addButton(new Button(x, y + BUTTON_HEIGHT + PADDING, buttonWidth, BUTTON_HEIGHT,
            getToggleButtonText("Enable Stone Age", settings.isStoneAgeEnabled()),
            button -> onStoneAgeToggle(button)));

        // Iron Tech Toggle
        this.addButton(new Button(x, y + (BUTTON_HEIGHT + PADDING) * 2, buttonWidth, BUTTON_HEIGHT,
            getToggleButtonText("Enable Iron Tech", settings.isIronTechEnabled()),
            button -> onIronTechToggle(button)));

        // Diamond Hunt Toggle
        this.addButton(new Button(x, y + (BUTTON_HEIGHT + PADDING) * 3, buttonWidth, BUTTON_HEIGHT,
            getToggleButtonText("Enable Diamond Hunt", settings.isDiamondHuntEnabled()),
            button -> onDiamondHuntToggle(button)));
    }

    private String getToggleButtonText(String label, boolean state) {
        return label + ": " + (state ? "ON" : "OFF");
    }

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        this.buttons.clear();
        this.children.clear();
        this.init();
    }

    private void onStartPressed() {
        if (settings.getLastPlan() != null) {
            controller.startPlan(settings.getLastPlan());
        }
    }

    private void onStopPressed() {
        controller.stop();
    }

    private void onWoodCollectionToggle(Button button) {
        boolean newState = !settings.isWoodCollectionEnabled();
        settings.setWoodCollectionEnabled(newState);
        button.setMessage(getToggleButtonText("Enable Wood Collection", newState));
    }

    private void onStoneAgeToggle(Button button) {
        boolean newState = !settings.isStoneAgeEnabled();
        settings.setStoneAgeEnabled(newState);
        button.setMessage(getToggleButtonText("Enable Stone Age", newState));
    }

    private void onIronTechToggle(Button button) {
        boolean newState = !settings.isIronTechEnabled();
        settings.setIronTechEnabled(newState);
        button.setMessage(getToggleButtonText("Enable Iron Tech", newState));
    }

    private void onDiamondHuntToggle(Button button) {
        boolean newState = !settings.isDiamondHuntEnabled();
        settings.setDiamondHuntEnabled(newState);
        button.setMessage(getToggleButtonText("Enable Diamond Hunt", newState));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
