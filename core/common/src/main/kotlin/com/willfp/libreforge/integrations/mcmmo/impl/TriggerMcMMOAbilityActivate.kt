package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerMcMMOAbilityActivate : Trigger("mcmmo_ability_activate") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerAbilityActivateEvent) {
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