package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.raid.RaidFinishEvent

class TriggerWinRaid : Trigger(
    "win_raid", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: RaidFinishEvent) {
        for (player in event.winners) {
            this.processTrigger(
                player,
                TriggerData(
                    player = player,
                    location = event.raid.location
                ),
                event.raid.badOmenLevel.toDouble() + 1
            )
        }
    }
}
