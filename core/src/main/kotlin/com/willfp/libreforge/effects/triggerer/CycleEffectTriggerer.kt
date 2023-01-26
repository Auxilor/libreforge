package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

class CycleEffectTriggerer : EffectTriggerer {
    private var offset = 0

    override fun trigger(list: Chain, player: Player, data: TriggerData) {
        offset %= list.size
        list[offset].trigger(player, data)
        offset++
    }
}
