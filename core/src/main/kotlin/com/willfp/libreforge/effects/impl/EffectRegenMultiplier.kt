package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.MultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent

object EffectRegenMultiplier : MultiplierEffect("regen_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity

        if (player !is Player) {
            return
        }

        event.amount *= getMultiplier(player)
    }
}
