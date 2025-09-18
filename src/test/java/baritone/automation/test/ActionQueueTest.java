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

package baritone.automation.test;

import baritone.automation.core.Action;
import baritone.automation.core.ActionQueue;
import baritone.automation.core.AutomationPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionQueueTest {
    private ActionQueue queue;

    @BeforeEach
    void setUp() {
        queue = new ActionQueue();
    }

    @Test
    void testQueueProcessing() {
        TestAction action1 = new TestAction("action1");
        TestAction action2 = new TestAction("action2");

        queue.queueAction(action1);
        queue.queueAction(action2);

        assertEquals(2, queue.getRemainingActionsCount());

        // Erste Tick sollte erste Action starten
        queue.processTick();
        assertTrue(action1.wasExecuted);
        assertFalse(action2.wasExecuted);

        // Action als beendet markieren
        action1.markDone();
        queue.processTick();

        // Zweite Action sollte jetzt starten
        assertTrue(action2.wasExecuted);
    }

    @Test
    void testClearQueue() {
        TestAction action = new TestAction("test");
        queue.queueAction(action);

        assertFalse(queue.isEmpty());
        queue.clear();
        assertTrue(queue.isEmpty());
        assertTrue(action.wasCancelled);
    }

    // Test-Implementierung einer Action
    private static class TestAction implements Action {
        private final String name;
        private boolean isDone = false;
        private boolean wasExecuted = false;
        private boolean wasCancelled = false;

        TestAction(String name) {
            this.name = name;
        }

        @Override
        public boolean execute() {
            wasExecuted = true;
            return true;
        }

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public void cancel() {
            wasCancelled = true;
        }

        @Override
        public String getDescription() {
            return name;
        }

        @Override
        public Action.ActionType getType() {
            return Action.ActionType.PATH;
        }

        void markDone() {
            isDone = true;
        }
    }
}
