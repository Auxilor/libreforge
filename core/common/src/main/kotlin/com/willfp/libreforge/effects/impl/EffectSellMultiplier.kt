package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

object EffectSellMultiplier : MultiplierEffect("sell_multiplier") {
    override val description = "Multiplies the sell price of items when the player sells through a shop."
    override val categories = setOf("economy")

    @EventHandler
    fun handle(event: ShopSellEvent) {
        event.multiplier *= getMultiplier(event.player.toDispatcher())
    }
}
