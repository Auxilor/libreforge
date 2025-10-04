package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import java.util.UUID

object TriggerCollideWithEntity : Trigger("collide_with_entity") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    private val cooldown = mutableMapOf<Pair<UUID, UUID>, Long>()
    private const val COLLISION_COOLDOWN_MS = 500L

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        for (entity in player.getNearbyEntities(1.0, 1.0, 1.0)) {
            val victim = entity as? LivingEntity ?: continue
            if (victim == player) continue

            val key = player.uniqueId to victim.uniqueId
            val now = System.currentTimeMillis()

            if (cooldown.getOrDefault(key, 0L) > now) continue
            cooldown[key] = now + COLLISION_COOLDOWN_MS

            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    victim = victim,
                    location = player.location
                )
            )
        }
    }
}