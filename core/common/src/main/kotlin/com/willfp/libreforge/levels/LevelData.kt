package com.willfp.libreforge.levels

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import org.bukkit.inventory.ItemStack

data class LevelData(
    val level: Int,
    val xp: Double
) {
    fun gainXP(type: LevelType, xp: Double, itemStack: ItemStack, context: PlaceholderContext): LevelData {
        var currentLevel = this.level
        var remaining = this.xp + xp

        while (true) {
            val required = type.getXPRequired(currentLevel, context)
            if (remaining < required) {
                return LevelData(currentLevel, remaining)
            }
            remaining -= required
            currentLevel++
            type.handleLevelUp(currentLevel, itemStack, context)
        }
    }
}
