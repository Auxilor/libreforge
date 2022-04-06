package com.willfp.libreforge.integrations.zshop

import com.willfp.libreforge.effects.Effects
import fr.maxlego08.shop.api.events.ZShopSellEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ZShopSellMultiplierHook : Listener {
    private val playerField = ZShopSellEvent::class.java.getDeclaredField("player").apply {
        isAccessible = true
    }

    private val priceField = ZShopSellEvent::class.java.getDeclaredField("price").apply {
        isAccessible = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: ZShopSellEvent) {
        setPrice(event, getPrice(event) * Effects.SELL_MULTIPLIER.getMultiplier(getPlayer(event)))
    }

    private fun getPlayer(event: ZShopSellEvent): Player = playerField.get(event) as Player
    private fun getPrice(event: ZShopSellEvent): Double = priceField.get(event) as Double
    private fun setPrice(event: ZShopSellEvent, price: Double) = priceField.set(event, price)
}
