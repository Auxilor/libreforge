package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfiguredProperty
import org.bukkit.entity.Player

/**
 * A single condition config block.
 */
class ConditionBlock<T>(
    val condition: Condition<T>,
    override val config: Config,
    override val compileData: T?
) : ConfiguredProperty<T> {
    fun isMet(player: Player) =
        condition.isMet(player, config, compileData)
}
