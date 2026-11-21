package com.willfp.libreforge.integrations.phoenixnpcs.impl

import com.phoenixplugins.phoenixnpcs.api.ClickType
import com.phoenixplugins.phoenixnpcs.api.events.NpcInteractEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerRightClickPhoenixNPC : Trigger("right_click_phoenix_npc") {
    override val description = "Fires when the player right-clicks a PhoenixNPCs NPC."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf("Requires PhoenixNPCs to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NpcInteractEvent) {
        if (event.click != ClickType.RIGHT) {
            return
        }

        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                location = event.npc.currentLocation,
                event = event
            )
        )
    }
}
