package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object RandomEffectTriggerer : EffectTriggerer {
    override fun trigger(list: Chain, player: Player, data: TriggerData) {
        list.random().trigger(player, data)
    }
}
