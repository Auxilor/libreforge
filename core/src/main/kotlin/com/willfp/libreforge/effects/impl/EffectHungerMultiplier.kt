package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.effects.MultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import kotlin.math.ceil

object EffectHungerMultiplier : MultiplierEffect("hunger_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        val diff = event.foodLevel - player.foodLevel

        if (diff >= 0) {
            return
        }

        val multiplier = getMultiplier(player)

        if (multiplier < 1) {
            if (NumberUtils.randFloat(0.0, 1.0) > multiplier) {
                event.isCancelled = true
            }
        } else {
            event.foodLevel = player.foodLevel + ceil(diff * getMultiplier(player)).toInt()
        }
    }
}
