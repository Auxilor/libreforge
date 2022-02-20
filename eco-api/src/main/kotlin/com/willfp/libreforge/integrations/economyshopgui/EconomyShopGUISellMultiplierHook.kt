package com.willfp.libreforge.integrations.economyshopgui

import com.willfp.libreforge.effects.Effects
import me.gypopo.economyshopgui.api.events.PreTransactionEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class EconomyShopGUISellMultiplierHook : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: PreTransactionEvent) {
        if (event.isCancelled) {
            return
        }

        if (event.transactionMode.equals("BUY", ignoreCase = true)) {
            return
        }

        event.price *= Effects.SELL_MULTIPLIER.getMultiplier(event.player)
    }
}
