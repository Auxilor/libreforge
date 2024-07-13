package com.willfp.libreforge.conditions

import com.willfp.eco.util.formatEco
import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.EmptyProvidedHolder.holder
import com.willfp.libreforge.GlobalDispatcher.dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.DispatchedTrigger
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
    fun getNotMetLines(dispatcher: Dispatcher<*>, holder: ProvidedHolder): List<String> {
        val lines = mutableListOf<String>()

        for (block in this) {
            if (block.notMetLines.isEmpty()) {
                continue
            }

            if (block.isMet(dispatcher, holder)) {
                continue
            }

            val conditionLines = block.notMetLines
            val context = block.config.applyHolder(holder, dispatcher).toPlaceholderContext()

            lines += conditionLines.map {
                it.formatEco(context)
            }
        }

        return lines
    }

    @Deprecated(
        "Use areMet(dispatcher, holder) instead.",
        ReplaceWith("areMet(player.toDispatcher(), holder)"),
        DeprecationLevel.ERROR
    )
    fun areMet(player: Player, holder: ProvidedHolder): Boolean =
        areMet(player.toDispatcher(), holder)

    /**
     * Get if any conditions are not met and should be shown.
     */
    @Deprecated(
        "Use isShowingAnyNotMet(dispatcher, holder) instead.",
        ReplaceWith("isShowingAnyNotMet(player.toDispatcher(), holder)"),
        DeprecationLevel.ERROR
    )
    fun isShowingAnyNotMet(player: Player, holder: ProvidedHolder): Boolean =
        isShowingAnyNotMet(player.toDispatcher(), holder)

    @Deprecated(
        "Use getNotMetLines(dispatcher, holder) instead.",
        ReplaceWith("getNotMetLines(player.toDispatcher(), holder)"),
        DeprecationLevel.ERROR
    )
    fun getNotMetLines(player: Player, holder: ProvidedHolder): List<String> =
        getNotMetLines(player.toDispatcher(), holder)
}

/**
 * Create an empty [ConditionList].
 */
fun emptyConditionList() = ConditionList(emptyList())
