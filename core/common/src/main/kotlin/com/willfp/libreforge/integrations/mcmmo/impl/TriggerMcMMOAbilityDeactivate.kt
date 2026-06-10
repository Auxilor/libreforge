package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerMcMMOAbilityDeactivate : Trigger("mcmmo_ability_deactivate") {
    override val description = "Fires when the player deactivates a McMMO ability."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires McMMO to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerAbilityDeactivateEvent) {
        val player = event.player
        val location = event.player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = location,
                event = event
            )
        )

    }
}