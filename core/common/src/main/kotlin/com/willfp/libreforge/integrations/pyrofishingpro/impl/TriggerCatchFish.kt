package com.willfp.libreforge.integrations.pyrofishingpro.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.arsmagica.API.PyroFishCatchEvent
import org.bukkit.event.EventHandler

object TriggerCatchFish : Trigger("pyro_catch_fish") {
    override val description = "Fires when the player catches a fish through PyroFishingPro."

    override val categories = setOf("fishing")

    override val additionalInfo = listOf("Requires PyroFishingPro to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The tier of the caught fish.",
        TriggerParameter.VALUE to "The fish number identifier."
    )

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