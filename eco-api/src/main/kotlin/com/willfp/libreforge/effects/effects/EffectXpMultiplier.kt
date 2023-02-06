package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiplierEffect
import com.willfp.libreforge.triggers.wrappers.WrappedXpEvent
import org.bukkit.event.EventHandler
import kotlin.math.ceil

class EffectXpMultiplier : GenericMultiplierEffect("xp_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        val multiplier = getMultiplier(player)

        val wrapped = WrappedXpEvent(event.expChangeEvent)
        wrapped.amount = ceil(wrapped.amount * multiplier).toInt()
    }
}
