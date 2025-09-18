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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

public class AutomationLogger {
    private static AutomationLogger INSTANCE;
    private final Logger logger;

    private AutomationLogger() {
        logger = Logger.getLogger("Baritone-Automation");
        logger.setLevel(Level.ALL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
    }

    public static AutomationLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutomationLogger();
        }
        return INSTANCE;
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void error(String message, Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }
}
