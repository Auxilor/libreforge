package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.EntityDispatcher
import org.bukkit.event.EventHandler
import kotlin.math.ceil

object EffectXpMultiplier : MultiplierEffect("xp_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        val multiplier = getMultiplier(EntityDispatcher(player))

        event.expChangeEvent.amount = ceil(event.expChangeEvent.amount * multiplier).toInt()
    }
}
