package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetStatChangeEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpLoseStat : Trigger("sp_lose_stat") {
    override val description = "Fires when a stat of one of the player's SmartPets pets goes down."

    override val categories = setOf("pets")

    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Stats are TRUST, LOYALTY, HUNGER, ENERGY, AFFECTION, HAPPINESS, FEAR and ANGER, from 0 to 100."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The amount the stat went down by.",
        TriggerParameter.TEXT to "The stat that changed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler
    fun handle(event: PetStatChangeEvent) {
        if (event.newValue >= event.oldValue) return

        dispatchPetEvent(event, -event.delta.toDouble(), event.stat.name)
    }
}
