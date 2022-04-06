package com.willfp.libreforge.integrations.zshop

import com.willfp.libreforge.effects.Effects
import fr.maxlego08.shop.api.events.ZShopSellEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ZShopSellMultiplierHook : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: ZShopSellEvent) {
        if (event.isCancelled) {
            return
        }

        event.price *= Effects.SELL_MULTIPLIER.getMultiplier(event.player)
    }
}
