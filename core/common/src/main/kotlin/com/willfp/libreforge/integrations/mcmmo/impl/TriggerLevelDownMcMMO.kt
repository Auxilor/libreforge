package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerLevelDownMcMMO : Trigger("level_down_mcmmo") {
    override val description = "Fires when the player loses a level in a McMMO skill."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires McMMO to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The new skill level."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerLevelDownEvent) {
        val player = event.player
        val location = event.player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = location,
                event = event,
                value = event.skillLevel.toDouble()
            )
        )
    }
}