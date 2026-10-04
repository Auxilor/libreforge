package com.willfp.libreforge.levels

import com.willfp.eco.core.progression.LevelCurve
import com.willfp.eco.core.progression.LevelProgression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

class LevelDataTest {
    /**
     * The libreforge half of the config-driven-infinite-loop finding: `LevelData.gainXP` was a
     * `while (true)` that never terminated when the curve returned <= 0. This asserts the
     * arithmetic underneath it now terminates; the ItemStack-dependent wrapper is covered by
     * the manual scenario in this task.
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    fun itemLevelGainTerminatesOnZeroRequirement() {
        val curve = LevelCurve.Formula("0", 1, Int.MAX_VALUE) { _, _ -> 0.0 }
        val change = LevelProgression.progress(curve, 1, 0.0, 100.0)

        assertEquals(1, change.newLevel)
    }
}
