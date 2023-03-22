package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.effects.EffectList
import org.bukkit.entity.Player

/**
 * A single condition config block.
 */
class ConditionBlock<T>(
    val condition: Condition<T>,
    override val config: Config,
    override val compileData: T,
    val notMetEffects: EffectList,
    val notMetLines: List<String>,
    val showNotMet: Boolean
) : Compiled<T> {
    fun isMet(player: Player): Boolean {
        return condition.isMet(player, config, compileData)
    }
}
