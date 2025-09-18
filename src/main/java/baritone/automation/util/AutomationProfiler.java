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

import java.util.HashMap;
import java.util.Map;

public class AutomationProfiler {
    private static final AutomationProfiler INSTANCE = new AutomationProfiler();
    private final Map<String, Long> startTimes = new HashMap<>();
    private final Map<String, Long> durations = new HashMap<>();
    private final AutomationLogger logger = AutomationLogger.getInstance();

    private AutomationProfiler() {}

    public static AutomationProfiler getInstance() {
        return INSTANCE;
    }

    public void startSection(String name) {
        startTimes.put(name, System.nanoTime());
    }

    public void endSection(String name) {
        Long start = startTimes.remove(name);
        if (start != null) {
            long duration = System.nanoTime() - start;
            durations.merge(name, duration, Long::sum);

            // Log wenn Aktion zu lange dauert
            if (duration > 100_000_000) { // 100ms
                logger.warning(String.format("Action '%s' took %.2fms", name, duration / 1_000_000.0));
            }
        }
    }

    public void reset() {
        startTimes.clear();
        durations.clear();
    }

    public Map<String, Double> getAverageDurations() {
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, Long> entry : durations.entrySet()) {
            averages.put(entry.getKey(), entry.getValue() / 1_000_000.0); // Konvertiere zu ms
        }
        return averages;
    }

    public String getProfileReport() {
        StringBuilder report = new StringBuilder("Automation Performance Report:\n");
        Map<String, Double> averages = getAverageDurations();

        averages.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                report.append(String.format("- %s: %.2fms\n",
                    entry.getKey(), entry.getValue()));
            });

        return report.toString();
    }
}
