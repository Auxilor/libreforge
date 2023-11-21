package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.PlayerDispatcher
import org.bukkit.event.EventHandler

object EffectSellMultiplier : MultiplierEffect("sell_multiplier") {
    @EventHandler
    fun handle(event: ShopSellEvent) {
        event.multiplier *= getMultiplier(PlayerDispatcher(event.player))
    }
}
