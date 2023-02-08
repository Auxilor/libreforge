package com.willfp.libreforge.integrations.tmmobcoins.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import net.devtm.tmmobcoins.API.MobCoinReceiveEvent
import org.bukkit.event.EventHandler

object EffectMobCoinsMultiplier : MultiplierEffect("mob_coins_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: MobCoinReceiveEvent) {
        val player = event.player

        event.setDropAmount(event.obtainedAmount * getMultiplier(player))
    }
}
