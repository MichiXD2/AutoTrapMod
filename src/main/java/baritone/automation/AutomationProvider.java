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

import baritone.api.IBaritone;
import baritone.automation.commands.AutomationCommand;
import baritone.automation.core.AutomationController;

/**
 * Integration point for the Baritone automation system.
 * Handles initialization and provides access to automation features.
 */
public class AutomationProvider {
    private static AutomationProvider INSTANCE;
    private final AutomationModule module;
    private final IBaritone baritone;

    private AutomationProvider(IBaritone baritone) {
        this.baritone = baritone;
        this.module = new AutomationModule();
        registerCommand();
    }

    public static void initialize(IBaritone baritone) {
        if (INSTANCE == null) {
            INSTANCE = new AutomationProvider(baritone);
        }
    }

    public static AutomationProvider getInstance() {
        return INSTANCE;
    }

    private void registerCommand() {
        baritone.getCommandManager().register(new AutomationCommand(baritone));
    }

    public AutomationController getController() {
        return AutomationController.getInstance();
    }
}
