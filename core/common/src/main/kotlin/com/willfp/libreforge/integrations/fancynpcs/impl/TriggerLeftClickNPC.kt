package com.willfp.libreforge.integrations.fancynpcs.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import de.oliver.fancynpcs.api.actions.ActionTrigger
import de.oliver.fancynpcs.api.events.NpcInteractEvent
import org.bukkit.event.EventHandler

object TriggerLeftClickNPC : Trigger("left_click_npc") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NpcInteractEvent) {
        if (event.interactionType == ActionTrigger.LEFT_CLICK) {
            val player = event.player ?: return

            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    event = event
                )
            )
        }
    }
}