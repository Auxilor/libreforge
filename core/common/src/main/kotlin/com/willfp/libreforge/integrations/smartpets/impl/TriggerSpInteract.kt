package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetInteractEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpInteract : Trigger("sp_interact") {
    override val description = "Fires when the player interacts with their SmartPets pet."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The level of the pet.",
        TriggerParameter.TEXT to "The type of interaction, such as FEED or PRAISE."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler
    fun handle(event: PetInteractEvent) {
        dispatchPetEvent(event, event.pet.level.toDouble(), event.type.name)
    }
}
