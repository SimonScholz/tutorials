package dev.simonscholz

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Duration

class DurationTest {

    @Test
    fun `parsing duration should not throw`() {
        assertDoesNotThrow { Duration.parse("PT20S") }
    }
}
