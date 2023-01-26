package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.entity.Player

/**
 * A single condition config block.
 */
class ConditionBlock<T>(
    val condition: Condition<T>,
    val config: Config,
    val compileData: T?
) {
    fun isMet(player: Player) =
        condition.isMet(player, config, compileData)
}
