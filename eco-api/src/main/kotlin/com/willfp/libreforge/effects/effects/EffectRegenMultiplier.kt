package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiplierEffect
import com.willfp.libreforge.triggers.wrappers.WrappedRegenEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent

class EffectRegenMultiplier : GenericMultiplierEffect("regen_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        val multiplier = getMultiplier(player)

        val wrapped = WrappedRegenEvent(event)
        wrapped.amount *= multiplier
    }
}
