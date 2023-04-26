package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.EffectList
import com.willfp.libreforge.generatePlaceholders
import com.willfp.libreforge.mapToPlaceholders
import com.willfp.libreforge.withHolder
import org.bukkit.entity.Player

/**
 * A single condition config block.
 */
class ConditionBlock<T> internal constructor(
    val condition: Condition<T>,
    override val config: Config,
    override val compileData: T,
    val notMetEffects: EffectList,
    val notMetLines: List<String>,
    val showNotMet: Boolean,
    val isInverted: Boolean
) : Compiled<T> {
    @Deprecated(
        "Use isMet(player, holder) instead.",
        ReplaceWith("condition.isMet(player, config, compileData)"),
        DeprecationLevel.ERROR
    )
    fun isMet(player: Player): Boolean {
        return isMet(player, EmptyProvidedHolder)
    }

    fun isMet(player: Player, holder: ProvidedHolder): Boolean {
        val withHolder = config.withHolder(holder)

        withHolder.injectPlaceholders(*holder.generatePlaceholders().mapToPlaceholders())

        val metWith = condition.isMet(player, withHolder, holder, compileData)
        val metWithout = condition.isMet(player, withHolder, compileData)

        return (metWith && metWithout) xor isInverted
    }
}
