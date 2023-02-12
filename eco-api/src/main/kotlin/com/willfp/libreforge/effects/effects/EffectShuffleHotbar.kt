package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
class EffectShuffleHotbar : Effect(
    "shuffle_hotbar",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim as? Player ?: return

        val hotbar = (0..8)
            .map { victim.inventory.getItem(it) }
            .shuffled()

        for ((index, item) in hotbar.withIndex()) {
            victim.inventory.setItem(index, item)
        }
    }
}
