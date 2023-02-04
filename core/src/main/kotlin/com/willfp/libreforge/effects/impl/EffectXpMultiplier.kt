package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.MultiplierEffect
import com.willfp.libreforge.triggers.wrappers.WrappedXpEvent
import org.bukkit.event.EventHandler
import kotlin.math.ceil

class EffectXpMultiplier : MultiplierEffect("xp_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.expChangeEvent.player

        val multiplier = getMultiplier(player)

        val wrapped = WrappedXpEvent(event.expChangeEvent)
        wrapped.amount = ceil(wrapped.amount * multiplier).toInt()
    }
}
