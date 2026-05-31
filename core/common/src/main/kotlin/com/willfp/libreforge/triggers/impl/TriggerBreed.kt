package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityBreedEvent

object TriggerBreed : Trigger("breed") {
    override val description = "Fires when two entities are bred."

    override val categories = setOf("entity")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The offspring entity that was spawned.",
        TriggerParameter.LOCATION to "The location of the offspring.",
        TriggerParameter.ITEM to "The item used to breed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityBreedEvent) {
        val breeder = event.breeder

        this.dispatch(
            breeder?.toDispatcher() ?: GlobalDispatcher,
            TriggerData(
                player = breeder as? Player,
                victim = event.entity,
                location = event.entity.location,
                item = event.bredWith,
                value = event.experience.toDouble()
            )
        )
    }
}
