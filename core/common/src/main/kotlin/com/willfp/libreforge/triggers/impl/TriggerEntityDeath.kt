package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent

object TriggerEntityDeath : Trigger("entity_death") {
    override val description = "Fires when any living entity dies."

    override val categories = setOf("entity")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that died.",
        TriggerParameter.LOCATION to "The location where the entity died."
    )

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDeathEvent) {
        val entity = event.entity as? LivingEntity ?: return

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                victim = entity,
                location = entity.location
            )
        )
    }
}
