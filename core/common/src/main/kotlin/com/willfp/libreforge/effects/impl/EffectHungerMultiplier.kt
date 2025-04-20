package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import kotlin.math.ceil

object EffectHungerMultiplier : MultiplierEffect("hunger_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        val entity = event.entity

        val diff = event.foodLevel - entity.foodLevel

        if (diff >= 0) {
            return
        }

        val multiplier = getMultiplier(entity.toDispatcher())

        if (multiplier < 1) {
            if (NumberUtils.randFloat(0.0, 1.0) > multiplier) {
                event.isCancelled = true
            }
        } else {
            event.foodLevel = entity.foodLevel + ceil(diff * getMultiplier(entity.toDispatcher())).toInt()
        }
    }
}
