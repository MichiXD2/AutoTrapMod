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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AutomationProfiler {
    private static AutomationProfiler INSTANCE;
    private final Map<String, Long> sectionTimes;
    private final Stack<String> sectionStack;
    private final Stack<Long> timeStack;

    private AutomationProfiler() {
        this.sectionTimes = new HashMap<>();
        this.sectionStack = new Stack<>();
        this.timeStack = new Stack<>();
    }

    public static AutomationProfiler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutomationProfiler();
        }
        return INSTANCE;
    }

    public void startSection(String name) {
        sectionStack.push(name);
        timeStack.push(System.nanoTime());
    }

    public void endSection() {
        if (!sectionStack.isEmpty() && !timeStack.isEmpty()) {
            String section = sectionStack.pop();
            long startTime = timeStack.pop();
            long duration = System.nanoTime() - startTime;

            sectionTimes.merge(section, duration, Long::sum);
        }
    }

    public Map<String, Long> getSectionTimes() {
        return new HashMap<>(sectionTimes);
    }

    public void reset() {
        sectionTimes.clear();
        sectionStack.clear();
        timeStack.clear();
    }
}
