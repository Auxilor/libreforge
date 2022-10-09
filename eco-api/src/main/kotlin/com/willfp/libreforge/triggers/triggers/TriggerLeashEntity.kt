package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerLeashEntityEvent

class TriggerLeashEntity : Trigger(
    "leash_entity", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLeashEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.entity.location,
                victim = event.entity as? LivingEntity,
                event = GenericCancellableEvent(event)
            )
        )
    }
}
