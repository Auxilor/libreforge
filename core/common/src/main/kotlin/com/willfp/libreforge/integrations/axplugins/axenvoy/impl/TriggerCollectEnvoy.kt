package com.willfp.libreforge.integrations.axplugins.axenvoy.impl

import com.artillexstudios.axenvoy.event.EnvoyCrateCollectEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCollectEnvoy : Trigger("collect_envoy") {
    override val description = "Fires when the player collects an AxEnvoy crate."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf("Requires AxEnvoy to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The finish location of the collected crate.",
        TriggerParameter.TEXT to "The name of the envoy crate type that was collected."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EnvoyCrateCollectEvent) {
        val player = event.player ?: return
        val crate = event.crate ?: return
        val location = crate.finishLocation ?: return
        val crateType = crate.handle?.name ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = location,
                event = event,
                text = crateType
            )
        )
    }
}