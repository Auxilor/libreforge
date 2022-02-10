package com.willfp.libreforge.integrations.deluxesellwands

import com.willfp.libreforge.effects.Effects
import dev.norska.dsw.api.DeluxeSellwandSellEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class DeluxeSellwandsSellMultiplierHook : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: DeluxeSellwandSellEvent) {
        if (event.isCancelled) {
            return
        }

        event.money *= Effects.SELL_MULTIPLIER.getMultiplier(event.player)
    }
}
