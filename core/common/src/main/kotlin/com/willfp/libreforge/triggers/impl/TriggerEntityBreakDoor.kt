package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityBreakDoorEvent

object TriggerEntityBreakDoor : Trigger("entity_break_door") {
    override val description = "Fires when an entity breaks a door."

    override val categories = setOf("entity")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that broke the door.",
        TriggerParameter.LOCATION to "The entity's location."
    )

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityBreakDoorEvent) {
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
