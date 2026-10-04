package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetExperienceGainEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpGainXp : Trigger("sp_gain_xp") {
    override val description = "Fires when one of the player's SmartPets pets gains experience."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The amount of experience gained.",
        TriggerParameter.TEXT to "The source of the experience, such as FEED or GUARD."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PetExperienceGainEvent) {
        dispatchPetEvent(event, event.amount.toDouble(), event.source.name)
    }
}
