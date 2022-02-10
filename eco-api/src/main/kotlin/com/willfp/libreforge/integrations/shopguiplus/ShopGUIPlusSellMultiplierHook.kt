package com.willfp.libreforge.integrations.shopguiplus

import com.willfp.libreforge.effects.Effects
import net.brcdev.shopgui.event.ShopPreTransactionEvent
import net.brcdev.shopgui.shop.ShopManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ShopGUIPlusSellMultiplierHook : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: ShopPreTransactionEvent) {
        if (event.isCancelled) {
            return
        }

        if (event.shopAction == ShopManager.ShopAction.BUY) {
            return
        }

        event.price *= Effects.SELL_MULTIPLIER.getMultiplier(event.player)
    }
}
