package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.MultiplierEffect
import org.bukkit.event.EventHandler
import kotlin.math.ceil

object EffectXpMultiplier : MultiplierEffect("xp_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        val multiplier = getMultiplier(player)

        event.expChangeEvent.amount = ceil(event.expChangeEvent.amount * multiplier).toInt()
    }
}
