package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntitySpawnEvent

object TriggerEntitySpawn : Trigger("entity_spawn") {
    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntitySpawnEvent) {
        if (!isEnabled) return
        val entity = event.entity

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                victim = entity as? LivingEntity,
                location = entity.location,
            )
        )
    }
}
