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

import baritone.automation.core.AutomationPlan;
import baritone.automation.core.PlanLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlanLoaderTest {
    private PlanLoader planLoader;
    private static final String TEST_PLAN = "test_plan";

    @BeforeEach
    void setUp() {
        planLoader = new PlanLoader();
    }

    @Test
    void testLoadValidPlan() {
        AutomationPlan plan = planLoader.loadPlan("full_diamond");
        assertNotNull(plan);
        assertEquals("Full Diamond", plan.getName());
        assertFalse(plan.getSteps().isEmpty());
    }

    @Test
    void testPlanFailsafes() {
        AutomationPlan plan = planLoader.loadPlan("full_diamond");
        AutomationPlan.FailsafeConfig failsafes = plan.getFailsafes();

        assertEquals(6, failsafes.getMinFood());
        assertEquals(6, failsafes.getMinHearts());
        assertEquals(10, failsafes.getToolMinDurabilityPct());
        assertTrue(failsafes.isAvoidLava());
    }

    @Test
    void testInvalidPlan() {
        AutomationPlan plan = planLoader.loadPlan("nonexistent_plan");
        assertNull(plan);
    }
}
