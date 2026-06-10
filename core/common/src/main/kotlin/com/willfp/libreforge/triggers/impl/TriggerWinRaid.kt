package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.raid.RaidFinishEvent

object TriggerWinRaid : Trigger("win_raid") {
    override val description = "Fires when the player wins a raid."

    override val categories = setOf("combat", "world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the raid.",
        TriggerParameter.VALUE to "The bad omen level of the raid, plus one."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: RaidFinishEvent) {
        for (player in event.winners) {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = event.raid.location,
                    value = event.raid.badOmenLevel.toDouble() + 1
                )
            )
        }
    }
}
