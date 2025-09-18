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

import baritone.automation.util.RetryHelper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

class RetryHelperTest {

    @Test
    void testRetrySuccessful() throws Exception {
        AtomicInteger attempts = new AtomicInteger(0);

        String result = RetryHelper.withRetry(
            () -> {
                attempts.incrementAndGet();
                return "success";
            },
            r -> true,
            3,
            100,
            "test action"
        );

        assertEquals("success", result);
        assertEquals(1, attempts.get());
    }

    @Test
    void testRetryEventualSuccess() throws Exception {
        AtomicInteger attempts = new AtomicInteger(0);

        String result = RetryHelper.withRetry(
            () -> {
                if (attempts.incrementAndGet() < 3) {
                    throw new RuntimeException("Temporary failure");
                }
                return "success";
            },
            r -> true,
            3,
            100,
            "test action"
        );

        assertEquals("success", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void testWaitUntilSuccess() {
        AtomicInteger counter = new AtomicInteger(0);

        assertDoesNotThrow(() -> {
            RetryHelper.waitUntil(
                unused -> counter.incrementAndGet() >= 3,
                1000,
                100,
                "wait test"
            );
        });

        assertTrue(counter.get() >= 3);
    }

    @Test
    void testWaitUntilTimeout() {
        assertThrows(RuntimeException.class, () -> {
            RetryHelper.waitUntil(
                unused -> false,
                500,
                100,
                "timeout test"
            );
        });
    }
}
