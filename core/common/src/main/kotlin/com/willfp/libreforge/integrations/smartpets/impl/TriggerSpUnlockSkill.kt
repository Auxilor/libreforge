package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetSkillUnlockEvent
import com.willfp.libreforge.integrations.smartpets.dispatchPetEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpUnlockSkill : Trigger("sp_unlock_skill") {
    override val description = "Fires when one of the player's SmartPets pets unlocks a skill."

    override val categories = setOf("pets")

    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The level of the pet.",
        TriggerParameter.TEXT to "The ID of the unlocked skill."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PetSkillUnlockEvent) {
        dispatchPetEvent(event, event.pet.level.toDouble(), event.skillId)
    }
}
