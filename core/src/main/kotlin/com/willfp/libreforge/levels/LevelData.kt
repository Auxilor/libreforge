package com.willfp.libreforge.levels

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import org.bukkit.inventory.ItemStack

data class LevelData(
    val level: Int,
    val xp: Double
) {
    fun gainXP(type: LevelType, xp: Double, itemStack: ItemStack, context: PlaceholderContext): LevelData {
        val required = type.getXPRequired(this.level, context)
        val current = this.xp

        return if (current + xp >= required) {
            val overshoot = current + xp - required

            val newData = LevelData(
                this.level + 1,
                0.0
            )

            type.handleLevelUp(
                this.level + 1,
                itemStack,
                context
            )

            newData.gainXP(type, overshoot, itemStack, context) // For recursive level gains.
        } else {
            LevelData(
                this.level,
                this.xp + xp
            )
        }
    }
}
