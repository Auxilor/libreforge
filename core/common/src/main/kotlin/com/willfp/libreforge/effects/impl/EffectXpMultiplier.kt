package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.events.DropQueuePushEvent
import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler
import kotlin.math.ceil

object EffectXpMultiplier : MultiplierEffect("xp_multiplier") {
    override val description = "Multiplies the amount of XP the player gains from natural sources and telekinesis drops."
    override val categories = setOf("economy")

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        val multiplier = getMultiplier(player.toDispatcher())

        event.expChangeEvent.amount = ceil(event.expChangeEvent.amount * multiplier).toInt()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: DropQueuePushEvent) {
        if (!event.isTelekinetic) return

        val player = event.player

        val multiplier = getMultiplier(player.toDispatcher())

        event.xp = ceil(event.xp * multiplier).toInt()
    }
}
