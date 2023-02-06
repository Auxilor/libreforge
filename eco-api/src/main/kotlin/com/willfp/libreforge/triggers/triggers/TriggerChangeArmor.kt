package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerChangeArmor : Trigger(
    "change_armor", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ArmorChangeEvent) {
        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            ),
        )
    }
}
