package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.triggers.BlankTrigger
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

/**
 * A list of effect blocks.
 */
class Chain(
    effects: List<ChainElement<*>>,
    private val executor: ChainExecutor
) : DelegatedList<ChainElement<*>>(
    effects.sortedBy {
        it.effect.runOrder.weight
    }
) {
    val weight = effects.sumOf { it.effect.runOrder.weight }

    fun trigger(
        trigger: DispatchedTrigger,
        executor: ChainExecutor = this.executor
    ): Boolean {
        return executor.execute(this, trigger)
    }

    fun trigger(
        player: Player,
        data: TriggerData = TriggerData(player = player),
        trigger: Trigger = BlankTrigger,
        executor: ChainExecutor = this.executor
    ): Boolean {
        return executor.execute(this, DispatchedTrigger(player, trigger, data))
    }
}
