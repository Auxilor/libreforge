package com.willfp.libreforge.conditions

import com.willfp.libreforge.DelegatedList
import org.bukkit.entity.Player

/**
 * A list of conditions.
 */
class ConditionList(
    conditions: List<ConditionBlock<*>>
) : DelegatedList<ConditionBlock<*>>() {
    init {
        this.list += conditions
    }

    fun areMet(player: Player): Boolean =
        this.all { it.isMet(player) }
}
