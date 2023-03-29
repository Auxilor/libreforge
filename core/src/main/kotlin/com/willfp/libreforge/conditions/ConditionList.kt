package com.willfp.libreforge.conditions

import com.willfp.eco.util.formatEco
import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
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
    fun areMet(player: Player, holder: ProvidedHolder): Boolean =
        this.all { it.isMet(player, holder) }

    /**
     * Get if all conditions are met, triggering effects if not.
     */
    fun areMetAndTrigger(trigger: DispatchedTrigger): Boolean =
        filterNot { it.isMet(trigger.player, trigger.data.holder) }
            .also { notMet ->
                if (notMet.isEmpty()) {
                    return true
                }

                notMet.forEach { it.notMetEffects.trigger(trigger) }
            }
            .let { false }

    /**
     * Get if any conditions are not met and should be shown.
     */
    fun isShowingAnyNotMet(player: Player, holder: ProvidedHolder): Boolean =
        this.getNotMetLines(player, holder).isNotEmpty()
                || this.any { it.showNotMet && !it.isMet(player, holder) }

    /**
     * Get all not met lines.
     */
    fun getNotMetLines(player: Player, holder: ProvidedHolder): List<String> =
        this.filter { !it.isMet(player, holder) }
            .flatMap { it.notMetLines }
            .map { it.formatEco(player, formatPlaceholders = true) }

    /**
     * Trigger all not met effects.
     */
    @Deprecated(
        "Use areMetAndTrigger(trigger) instead. This will be removed in a future release.",
        ReplaceWith("this.areMetAndTrigger(trigger)"),
        DeprecationLevel.ERROR
    )
    fun triggerNotMetEffects(trigger: DispatchedTrigger) =
        this.forEach { it.notMetEffects.trigger(trigger) }

    @Deprecated(
        "Use getNotMetLines(player, holder) instead. This will be removed in a future release.",
        ReplaceWith("this.getNotMetLines(player, EmptyProvidedHolder)"),
        DeprecationLevel.ERROR
    )
    fun getNotMetLines(player: Player): List<String> =
        this.getNotMetLines(player, EmptyProvidedHolder)

    @Deprecated(
        "Use areMet(player, holder) instead. This will be removed in a future release.",
        ReplaceWith("this.all { it.isMet(player) }"),
        DeprecationLevel.ERROR
    )
    fun areMet(player: Player): Boolean =
        this.areMet(player, EmptyProvidedHolder)

    @Deprecated(
        "Use isShowingAnyNotMet(player, holder) instead. This will be removed in a future release.",
        ReplaceWith("this.any { it.showNotMet && !it.isMet(player) }"),
        DeprecationLevel.ERROR
    )
    fun isShowingAnyNotMet(player: Player): Boolean =
        this.isShowingAnyNotMet(player, EmptyProvidedHolder)
}

/**
 * Create an empty [ConditionList].
 */
fun emptyConditionList() = ConditionList(emptyList())
