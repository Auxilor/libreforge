package com.willfp.libreforge.conditions

import com.willfp.eco.util.formatEco
import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.EmptyProvidedHolder.holder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.GlobalDispatcher.dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player

/**
 * A list of conditions.
 */
class ConditionList(
    conditions: List<ConditionBlock<*>>
) : DelegatedList<ConditionBlock<*>>(conditions) {
    /**
     * Get if all conditions are met.
     */
    fun areMet(player: Player, holder: ProvidedHolder): Boolean =
        this.all { it.isMet(player, holder) }

    /**
     * Get if all conditions are met.
     */
    fun areMet(dispatcher: Dispatcher<*>, holder: ProvidedHolder): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return this.all { it.isMet(player, holder) }
    }
    /**
     * Get if all conditions are met, triggering effects if not.
     */
    fun areMetAndTrigger(trigger: DispatchedTrigger): Boolean =
        filterNot { it.isMet(trigger.dispatcher, trigger.data.holder) }
            .also { notMet ->
                if (notMet.isEmpty()) {
                    return true
                }

                notMet.forEach { it.notMetEffects?.trigger(trigger) }
            }
            .let { false }

    /**
     * Get if any conditions are not met and should be shown.
     */
    fun isShowingAnyNotMet(player: Player, holder: ProvidedHolder): Boolean =
        this.any { it.showNotMet && !it.isMet(player, holder) }

    /**
     * Get all not met lines.
     */
    fun getNotMetLines(player: Player, holder: ProvidedHolder): List<String> =
        this.filter { it.notMetLines.isNotEmpty() }
            .filter { !it.isMet(player, holder) }
            .flatMap { it.notMetLines }
            .map { it.formatEco(player, formatPlaceholders = true) }
}

/**
 * Create an empty [ConditionList].
 */
fun emptyConditionList() = ConditionList(emptyList())
