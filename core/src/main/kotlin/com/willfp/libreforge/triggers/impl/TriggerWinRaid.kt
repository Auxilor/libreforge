package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.raid.RaidFinishEvent

object TriggerWinRaid : Trigger("win_raid") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: RaidFinishEvent) {
        if (!this.isEnabled) return

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
