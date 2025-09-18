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

package baritone.automation.commands;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandException;
import baritone.automation.core.AutomationController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AutomationCommand extends Command {

    public AutomationCommand(IBaritone baritone) {
        super(baritone, "auto", "automation");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String subcommand = args.getString();
        AutomationController controller = AutomationController.getInstance();

        switch (subcommand.toLowerCase()) {
            case "start":
                if (args.hasAny()) {
                    String planName = args.getString();
                    controller.startPlan(planName);
                    logDirect("Starting automation plan: " + planName);
                } else {
                    throw new CommandException("Please specify a plan name");
                }
                break;

            case "stop":
                controller.stop();
                logDirect("Stopped automation");
                break;

            case "pause":
                controller.pause();
                logDirect("Paused automation");
                break;

            case "resume":
                controller.resume();
                logDirect("Resumed automation");
                break;

            case "status":
                logStatus(controller);
                break;

            case "set":
                if (args.hasAtMost(2)) {
                    String option = args.getString();
                    String value = args.getString();
                    handleSetOption(option, value);
                } else {
                    throw new CommandException("Usage: auto set <option> <value>");
                }
                break;

            default:
                throw new CommandException("Unknown subcommand: " + subcommand);
        }
    }

    private void logStatus(AutomationController controller) {
        logDirect(String.format("Automation Status: %s", controller.getCurrentState()));
        if (controller.getCurrentState() != AutomationController.AutomationState.IDLE) {
            // Weitere Status-Details hier
        }
    }

    private void handleSetOption(String option, String value) throws CommandException {
        // Implementierung der Option-Änderungen hier
        logDirect(String.format("Set %s to %s", option, value));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        if (args.hasExactly(1)) {
            return Stream.of("start", "stop", "pause", "resume", "status", "set");
        }
        return Stream.empty();
    }

    @Override
    public List<String> getUsageExamples() {
        return Arrays.asList(
            "auto start <plan>",
            "auto stop",
            "auto pause",
            "auto resume",
            "auto status",
            "auto set <option> <value>"
        );
    }
}
