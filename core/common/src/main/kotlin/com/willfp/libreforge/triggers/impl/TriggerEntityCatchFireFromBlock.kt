package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityCombustByBlockEvent

object TriggerEntityCatchFireFromBlock : Trigger("entity_catch_fire_from_block") {
    override val description = "Fires when an entity catches fire from a block such as lava."

    override val categories = setOf("entity")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that caught fire.",
        TriggerParameter.LOCATION to "The entity's location."
    )

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityCombustByBlockEvent) {
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
