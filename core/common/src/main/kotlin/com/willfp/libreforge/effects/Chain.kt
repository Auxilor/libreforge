package com.willfp.libreforge.effects

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.WeightedList
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerBlank

/**
 * A list of effect blocks.
 */
class Chain internal constructor(
    effects: WeightedList<ChainElement<*>>,
    private val executor: ChainExecutor
) : WeightedList<ChainElement<*>>(
    effects.sortedBy {
        it.runOrder.weight
    }
) {
    val weight = effects.sumOf { it.runOrder.weight }

    fun trigger(
        trigger: DispatchedTrigger,
        executor: ChainExecutor = this.executor
    ): Boolean {
        val triggerWithDispatcher = if (trigger.data.dispatcher == trigger.dispatcher) {
            trigger
        } else {
            DispatchedTrigger(
                trigger.dispatcher,
                trigger.trigger,
                trigger.data.copy(dispatcher = trigger.dispatcher)
            ).inheritPlaceholders(trigger)
        }

        return executor.execute(this, triggerWithDispatcher)
    }

    fun trigger(
        dispatcher: Dispatcher<*>,
        data: TriggerData = TriggerData(dispatcher = dispatcher, player = dispatcher.get()),
        trigger: Trigger = TriggerBlank,
        executor: ChainExecutor = this.executor
    ): Boolean {
        return trigger(DispatchedTrigger(dispatcher, trigger, data), executor)
    }
}
