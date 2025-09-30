package com.willfp.libreforge.effects

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.WeightedList
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerBlank
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
        return executor.execute(this, trigger)
    }

    fun trigger(
        dispatcher: Dispatcher<*>,
        data: TriggerData = TriggerData(player = dispatcher.get()),
        trigger: Trigger = TriggerBlank,
        executor: ChainExecutor = this.executor
    ): Boolean {
        return executor.execute(this, DispatchedTrigger(dispatcher, trigger, data))
    }

    @Deprecated(
        "Use trigger(Dispatcher<*>, TriggerData, Trigger, ChainExecutor)",
        ReplaceWith("trigger(player.toDispatcher(), data, trigger, executor)"),
        DeprecationLevel.ERROR
    )
    fun trigger(
        player: Player,
        data: TriggerData = TriggerData(player = player),
        trigger: Trigger = TriggerBlank,
        executor: ChainExecutor = this.executor
    ): Boolean {
        return trigger(player.toDispatcher(), data, trigger, executor)
    }

    override fun toString(): String {
        return list.toString();
    }
}
