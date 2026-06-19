package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.api.JobsLevelUpEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerJobsLevelUp : Trigger("jobs_level_up") {
    override val description = "Fires when the player gains a level in a Jobs Reborn job."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires Jobs Reborn to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The new job level."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsLevelUpEvent) {
        val player = event.player.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                value = event.level.toDouble()
            )
        )
    }
}
