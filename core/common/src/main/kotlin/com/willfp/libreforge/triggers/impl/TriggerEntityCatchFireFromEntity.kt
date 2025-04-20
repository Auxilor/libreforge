package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityCombustByEntityEvent

object TriggerEntityCatchFireFromEntity : Trigger("entity_catch_fire_from_entity") {
    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityCombustByEntityEvent) {
        val entity = event.entity

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                victim = event.combuster as? LivingEntity,
                location = entity.location,
            )
        )
    }
}
