package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.triggerer.EffectTriggerer
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

/**
 * A list of effect blocks.
 */
class Chain(
    effects: List<EffectBlock<*>>,
    private val triggerer: EffectTriggerer
) : DelegatedList<EffectBlock<*>>() {
    init {
        for (order in RunOrder.values()) {
            this.list += effects.filter { it.effect.runOrder == order }
        }
    }

    fun trigger(player: Player, data: TriggerData) {
        triggerer.trigger(this, player, data)
    }
}
