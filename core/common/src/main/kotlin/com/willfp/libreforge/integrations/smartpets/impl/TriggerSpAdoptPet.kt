package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetAdoptEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpAdoptPet : Trigger("sp_adopt_pet") {
    override val description = "Fires when the player adopts a SmartPets pet."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The level of the pet.",
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
    fun handle(event: PetAdoptEvent) {
        dispatchPetEvent(event, event.pet.level.toDouble(), event.pet.type)
    }
}
