package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object NormalEffectTriggerer : EffectTriggerer {
    override fun trigger(list: Chain, player: Player, data: TriggerData) {
        for (block in list) {
            block.trigger(player, data)
        }
    }
}
