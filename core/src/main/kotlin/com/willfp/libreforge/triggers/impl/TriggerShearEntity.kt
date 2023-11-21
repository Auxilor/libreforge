package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerShearEntityEvent

object TriggerShearEntity : Trigger("shear_entity") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerShearEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return

        if (entity.type !in listOf(EntityType.SHEEP, EntityType.MUSHROOM_COW)) {
            return
        }

        this.dispatch(
            EntityDispatcher(event.player),
            TriggerData(
                player = event.player,
                victim = entity,
                location = entity.location,
                event = event,
            )
        )
    }
}
