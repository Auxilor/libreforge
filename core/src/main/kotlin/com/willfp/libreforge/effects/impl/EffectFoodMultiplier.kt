package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiplierEffect
import com.willfp.libreforge.triggers.wrappers.WrappedHungerEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import kotlin.math.ceil

class EffectFoodMultiplier : GenericMultiplierEffect("food_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity

        if (player !is Player) {
            return
        }

        val multiplier = getMultiplier(player)

        val wrapped = WrappedHungerEvent(event)

        if (wrapped.amount >= 0) {
            return
        }

        wrapped.amount = ceil(wrapped.amount * multiplier).toInt()
    }
}
