package com.willfp.libreforge.levels

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.progression.LevelProgression
import org.bukkit.inventory.ItemStack

data class LevelData(
    val level: Int,
    val xp: Double
) {
    /**
     * Apply an XP gain, returning the new data.
     *
     * The previous implementation was `while (true) { ... }`, which never terminated when the
     * configured xp-formula evaluated to <= 0 at any level - a single bad config value hung
     * the main thread with no crash log. The shared progression loop cannot do that.
     */
    fun gainXP(
        type: LevelType,
        xp: Double,
        itemStack: ItemStack,
        context: PlaceholderContext
    ): LevelData {
        val change = LevelProgression.progress(type.curve, this.level, this.xp, xp)

        change.levelsGained?.forEach { level ->
            type.handleLevelUp(level, itemStack, context)
        }

        return LevelData(change.newLevel, change.newXp)
    }
}
