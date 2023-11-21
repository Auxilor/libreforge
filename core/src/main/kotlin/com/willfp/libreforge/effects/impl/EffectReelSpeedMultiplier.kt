package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.EntityDispatcher
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

object EffectReelSpeedMultiplier : MultiplierEffect("reel_speed_multiplier") {
    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun handle(event: PlayerFishEvent) {
        if (!(event.state == PlayerFishEvent.State.CAUGHT_FISH || event.state == PlayerFishEvent.State.CAUGHT_ENTITY)) {
            return
        }

        val player = event.player

        val multiplier = getMultiplier(EntityDispatcher(player))

        if (multiplier == 1.0) {
            return
        }

        val vector = player.eyeLocation.toVector()
            .clone()
            .subtract(event.caught?.location?.toVector() ?: return)
            .normalize()
            .multiply(multiplier)

        plugin.scheduler.run {
            event.caught?.velocity = vector
        }
    }
}
