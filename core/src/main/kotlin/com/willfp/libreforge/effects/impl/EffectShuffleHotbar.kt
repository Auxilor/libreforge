package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectShuffleHotbar : Effect<NoCompileData>("shuffle_hotbar") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim as? Player ?: return false

        val hotbar = (0..9)
            .map { victim.inventory.getItem(it) }
            .shuffled()

        for ((index, item) in hotbar.withIndex()) {
            victim.inventory.setItem(index, item)
        }

        return true
    }
}
