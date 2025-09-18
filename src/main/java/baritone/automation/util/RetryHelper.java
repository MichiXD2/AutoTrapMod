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

package baritone.automation.util;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class RetryHelper {
    private final AutomationLogger logger = AutomationLogger.getInstance();

    public static <T> T withRetry(Callable<T> action,
                                 Predicate<T> successCheck,
                                 int maxAttempts,
                                 long delayMs,
                                 String actionName) throws Exception {
        Exception lastException = null;
        T result = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                result = action.call();
                if (successCheck.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;
                AutomationLogger.getInstance().warning(
                    String.format("Attempt %d/%d for '%s' failed: %s",
                        attempt, maxAttempts, actionName, e.getMessage())
                );
            }

            if (attempt < maxAttempts) {
                Thread.sleep(delayMs);
            }
        }

        if (lastException != null) {
            throw new RuntimeException(
                String.format("Action '%s' failed after %d attempts",
                    actionName, maxAttempts),
                lastException
            );
        }

        return result;
    }

    public static void waitUntil(Predicate<Void> condition,
                               long timeoutMs,
                               long checkIntervalMs,
                               String description) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        while (!condition.test(null)) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new RuntimeException(
                    String.format("Timeout waiting for condition: %s", description)
                );
            }
            Thread.sleep(checkIntervalMs);
        }
    }
}
