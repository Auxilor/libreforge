package com.willfp.libreforge.integrations.tmmobcoins

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiplierEffect
import net.devtm.tmmobcoins.API.MobCoinReceiveEvent
import org.bukkit.event.EventHandler

class EffectMobCoinsMultiplier : GenericMultiplierEffect("mob_coins_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: MobCoinReceiveEvent) {
        val player = event.player

        val multiplier = getMultiplier(player)

        event.setDropAmount(event.obtainedAmount * multiplier)
    }
}
