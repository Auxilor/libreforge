package com.willfp.libreforge.integrations.ultimatemobcoins.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import nl.chimpgamer.ultimatemobcoins.paper.events.MobCoinsReceiveEvent
import org.bukkit.event.EventHandler

object EffectMobCoinsDropMultiplier : MultiplierEffect("mob_coins_drop_multiplier") {
    override val description = "Multiplies the amount of UltimateMobCoins received from mob kills while the holder is active."
    override val categories = setOf("economy")

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MobCoinsReceiveEvent) {
        val player = event.player

        event.amount = event.amount.multiply(getMultiplier(player.toDispatcher()).toBigDecimal())
    }
}
