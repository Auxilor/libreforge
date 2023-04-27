package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.UUID

/**
 * Things that are like a chain element (e.g. Blocks, Elements).
 */
abstract class ElementLike {
    abstract val uuid: UUID
    abstract val config: Config
    abstract val arguments: EffectArgumentList
    abstract val conditions: ConditionList
    abstract val mutators: MutatorList
    abstract val filters: FilterList

    abstract val supportsDelay: Boolean

    /**
     * If the element is its own chain, (e.g. has an ID specified directly at the top level).
     */
    open val isElementOwnChain: Boolean = false

    /*
    The replacement for the old ConfiguredEffect#invoke method.

    The idea was originally everything would be clean and tidy here,
    but then while re-adding features and compatibility, it ended
    up not really being that. Whatever, I think it's good enough - it's
    definitely at least a lot better.
     */

    /**
     * Mutate, filter, and then trigger.
     */
    fun trigger(trigger: DispatchedTrigger): Boolean {
        // If own chain, defer all to elements.
        if (isElementOwnChain) {
            return doTrigger(trigger)
        }

        // It would be nice to abstract repeat/delay away here, but that would be
        // really, really, overengineering it - even for me.
        val repeatTimes = config.getIntFromExpression("repeat.times", trigger.data).coerceAtLeast(1)
        val repeatStart = config.getDoubleFromExpression("repeat.start", trigger.data)
        val repeatIncrement = config.getDoubleFromExpression("repeat.increment", trigger.data)
        var repeatCount = repeatStart

        trigger.addPlaceholder(NamedValue("repeat_times", repeatTimes))
        trigger.addPlaceholder(NamedValue("repeat_start", repeatStart))
        trigger.addPlaceholder(NamedValue("repeat_increment", repeatIncrement))
        trigger.addPlaceholder(NamedValue("repeat_count", repeatCount))

        val delay = config.getIntFromExpression("delay", trigger.data)
            .coerceAtLeast(0)
            .let { if (!supportsDelay) 0 else it }
            .toLong()

        // Extremely janky code, but it's cleaner than repeating myself 5 times I think.
        listOf(arguments, conditions, mutators, filters)
            .flatten()
            .map { it.config }
            .plusElement(config)
            .forEach { it.addInjectablePlaceholder(trigger.placeholders) }

        val data = mutators.mutate(trigger.data)

        // Antigrief check here - not very clean, but it works.
        if (data.player != null && data.victim != null && data.victim != data.player) {
            if (!config.getBool("disable_antigrief_check")) {
                if (!AntigriefManager.canInjure(data.player, data.victim)) {
                    return false
                }
            }
        }

        // Filter
        val filterResult = if (config.getBool("filters_before_mutation")) {
            filters.isMet(trigger.data)
        } else {
            filters.isMet(data)
        }

        if (!filterResult) {
            return false
        }

        if (!shouldTrigger(trigger.copy(data = data))) {
            return false
        }

        val (argumentsMet, met, notMet) = arguments.checkMet(this, trigger)

        // Only execute not met effects if the arguments were met.
        if (argumentsMet) {
            // Check conditions
            if (!conditions.areMetAndTrigger(trigger)) {
                return false
            }
        } else {
            notMet.forEach { it.ifNotMet(this, trigger) }
            return false
        }

        var didTrigger = false

        fun trigger() {
            // Set to true if triggered.
            didTrigger = if (doTrigger(
                    trigger.copy(
                        // Mutate again here for each repeat.
                        data = mutators.mutate(trigger.data)
                    )
                )
            ) true else didTrigger
        }

        // Can't delay initial execution for things that modify events.
        if (delay == 0L) {
            repeat(repeatTimes) {
                trigger()
            }

            repeatCount += repeatIncrement
        } else {
            // Delay between each repeat.
            var repeats = 0
            plugin.runnableFactory.create { task ->
                repeats++
                trigger()

                if (repeats >= repeatTimes) {
                    task.cancel()
                }
            }.runTaskTimer(delay, delay)
        }

        // Code here is fucking disgusting duplicating the delay check.
        // Whatever.

        // Only run met conditions if the trigger was actually successful.
        if (didTrigger || delay > 0) {
            met.forEach { it.ifMet(this, trigger) }
        }

        return didTrigger || delay > 0
    }

    protected abstract fun doTrigger(trigger: DispatchedTrigger): Boolean

    protected open fun shouldTrigger(trigger: DispatchedTrigger): Boolean {
        return true
    }
}
