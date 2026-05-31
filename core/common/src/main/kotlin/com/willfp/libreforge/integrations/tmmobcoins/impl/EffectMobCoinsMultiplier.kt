package com.willfp.libreforge.integrations.tmmobcoins.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import net.devtm.tmmobcoins.API.MobCoinReceiveEvent
import org.bukkit.event.EventHandler

object EffectMobCoinsMultiplier : MultiplierEffect("mob_coins_multiplier") {
    override val description = "Multiplies TMMobCoins earned from mob kills while the holder is active."
    override val categories = setOf("economy")
    @EventHandler(ignoreCancelled = true)
    fun handle(event: MobCoinReceiveEvent) {
        val player = event.player

        event.setDropAmount(event.obtainedAmount * getMultiplier(player.toDispatcher()))
    }
}
