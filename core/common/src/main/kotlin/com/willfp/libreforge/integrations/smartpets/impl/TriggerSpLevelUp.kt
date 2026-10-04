package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetLevelUpEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpLevelUp : Trigger("sp_level_up") {
    override val description = "Fires when one of the player's SmartPets pets levels up."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The new level of the pet.",
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
    fun handle(event: PetLevelUpEvent) {
        dispatchPetEvent(event, event.newLevel.toDouble(), event.pet.type)
    }
}
