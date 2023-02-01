package com.willfp.libreforge.triggers.triggers

import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerShearEntityEvent

class TriggerShearEntity: Trigger(
    "shear_entity", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VICTIM
    )
) {

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerShearEntityEvent) {
        if (event.entity.type != EntityType.SHEEP && event.entity.type != EntityType.MUSHROOM_COW) {
            return
        }
        this.processTrigger(
            event.player,
            TriggerData(
                player = event.player,
                location = event.entity.location,
                event = GenericCancellableEvent(event),
                victim = event.entity as LivingEntity
            )
        )
    }

}