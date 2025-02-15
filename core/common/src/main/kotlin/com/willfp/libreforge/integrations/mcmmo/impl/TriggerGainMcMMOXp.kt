package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainMcMMOXp : Trigger("gain_mcmmo_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerXpGainEvent) {
        val player = event.player
        val location = event.player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = location,
                event = event,
                value = event.rawXpGained.toDouble()
            )
        )
    }
}