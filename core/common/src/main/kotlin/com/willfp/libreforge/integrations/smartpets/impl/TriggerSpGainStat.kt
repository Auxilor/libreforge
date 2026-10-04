package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetStatChangeEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpGainStat : Trigger("sp_gain_stat") {
    override val description = "Fires when a stat of one of the player's SmartPets pets goes up."

    override val categories = setOf("pets")

    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Stats are TRUST, LOYALTY, HUNGER, ENERGY, AFFECTION, HAPPINESS, FEAR and ANGER, from 0 to 100."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The amount the stat went up by.",
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
        if (!event.isIncrease) return

        dispatchPetEvent(event, event.delta.toDouble(), event.stat.name)
    }
}
