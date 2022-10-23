package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.effects.GenericMultiplierEffect
import com.willfp.libreforge.triggers.wrappers.WrappedHungerEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import kotlin.math.ceil

class EffectHungerMultiplier : GenericMultiplierEffect("hunger_multiplier") {
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

        if (wrapped.amount <= 0) {
            return
        }

        if (multiplier < 1) {
            if (NumberUtils.randFloat(0.0, 1.0) > multiplier) {
                wrapped.isCancelled = true
            }
        } else {
            wrapped.amount = ceil(wrapped.amount * multiplier).toInt()
        }
    }
}
