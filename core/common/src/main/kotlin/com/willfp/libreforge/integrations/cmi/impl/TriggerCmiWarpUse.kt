package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPlayerWarpEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiWarpUse : Trigger("cmi_warp_use") {
    override val description = "Fires when the player uses a warp."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the warp.",
        TriggerParameter.TEXT to "The name of the warp."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIPlayerWarpEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.warp?.loc?.bukkitLoc,
                text = event.warp?.name
            )
        )
    }
}
