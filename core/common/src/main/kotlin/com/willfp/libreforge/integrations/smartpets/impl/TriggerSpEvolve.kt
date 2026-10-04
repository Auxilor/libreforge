package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetEvolveEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpEvolve : Trigger("sp_evolve") {
    override val description = "Fires when one of the player's SmartPets pets evolves."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The new evolution stage of the pet.",
        TriggerParameter.TEXT to "The type of the pet."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PetEvolveEvent) {
        dispatchPetEvent(event, event.newStage.toDouble(), event.pet.type)
    }
}
