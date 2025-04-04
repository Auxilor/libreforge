package com.willfp.libreforge.integrations.ultimatemobcoins.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import nl.chimpgamer.ultimatemobcoins.paper.events.PrepareMobCoinDropEvent
import org.bukkit.event.EventHandler

object EffectMobCoinsChanceMultiplier : MultiplierEffect("mob_coins_chance_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PrepareMobCoinDropEvent) {
        val player = event.player

        event.mobCoin.chance *= getMultiplier(player.toDispatcher())
    }
}
