package com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.arsmagica.API.PyroFishCatchEvent
import org.bukkit.event.EventHandler

object TriggerCatchFish : Trigger("pyro_catch_fish") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE
    )

    @EventHandler
    fun handle(event: PyroFishCatchEvent) {
        val player = event.a() ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = player.location,
                text = event.tier,
                value = event.fishNumber.toDouble()
            )
        )
    }
}