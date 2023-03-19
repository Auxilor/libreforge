package com.willfp.libreforge.conditions

import com.willfp.eco.util.formatEco
import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player

/**
 * A list of conditions.
 */
class ConditionList(
    conditions: List<ConditionBlock<*>>
) : DelegatedList<ConditionBlock<*>>(conditions) {
    fun areMet(player: Player): Boolean =
        this.all { it.isMet(player) }

    fun triggerNotMetEffects(trigger: DispatchedTrigger) =
        this.forEach { it.notMetEffects.trigger(trigger) }

    fun getNotMetLines(player: Player): List<String> =
        this.flatMap { it.notMetLines }
            .map { it.formatEco(player, formatPlaceholders = true) }
}

/**
 * Create an empty [ConditionList].
 */
fun emptyConditionList() = ConditionList(emptyList())
