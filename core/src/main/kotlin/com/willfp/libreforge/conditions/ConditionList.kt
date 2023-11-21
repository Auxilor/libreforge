package com.willfp.libreforge.conditions

import com.willfp.eco.util.formatEco
import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.get
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
    fun areMet(dispatcher: Dispatcher<*>, holder: ProvidedHolder): Boolean =
        this.all { it.isMet(dispatcher, holder) }

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
    fun isShowingAnyNotMet(dispatcher: Dispatcher<*>, holder: ProvidedHolder): Boolean =
        this.any { it.showNotMet && !it.isMet(dispatcher, holder) }

    /**
     * Get all not met lines.
     */
    fun getNotMetLines(dispatcher: Dispatcher<*>, holder: ProvidedHolder): List<String> =
        this.filter { it.notMetLines.isNotEmpty() }
            .filter { !it.isMet(dispatcher, holder) }
            .flatMap { it.notMetLines }
            .map { it.formatEco(dispatcher.get(), formatPlaceholders = true) }


    @Deprecated(
        "Use areMet(dispatcher, holder) instead.",
        ReplaceWith("areMet(dispatcher, holder)"),
        DeprecationLevel.ERROR
    )
    fun areMet(player: Player, holder: ProvidedHolder): Boolean =
        areMet(PlayerDispatcher(player), holder)

    /**
     * Get if any conditions are not met and should be shown.
     */
    @Deprecated(
        "Use isShowingAnyNotMet(dispatcher, holder) instead.",
        ReplaceWith("isShowingAnyNotMet(dispatcher, holder)"),
        DeprecationLevel.ERROR
    )
    fun isShowingAnyNotMet(player: Player, holder: ProvidedHolder): Boolean =
        isShowingAnyNotMet(PlayerDispatcher(player), holder)

    @Deprecated(
        "Use getNotMetLines(dispatcher, holder) instead.",
        ReplaceWith("getNotMetLines(dispatcher, holder)"),
        DeprecationLevel.ERROR
    )
    fun getNotMetLines(player: Player, holder: ProvidedHolder): List<String> =
        getNotMetLines(PlayerDispatcher(player), holder)
}

/**
 * Create an empty [ConditionList].
 */
fun emptyConditionList() = ConditionList(emptyList())
