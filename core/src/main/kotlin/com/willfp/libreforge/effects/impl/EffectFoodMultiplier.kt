package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import kotlin.math.ceil

object EffectFoodMultiplier : MultiplierEffect("food_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        val diff = event.foodLevel - player.foodLevel

        if (diff <= 0) {
            return
        }

        event.foodLevel = player.foodLevel + ceil(getMultiplier(player) * diff).toInt()
    }
}
