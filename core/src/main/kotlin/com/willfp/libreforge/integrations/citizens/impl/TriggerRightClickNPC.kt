package com.willfp.libreforge.integrations.citizens.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler

object TriggerRightClickNPC : Trigger("right_click_npc") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NPCRightClickEvent) {
        val player = event.clicker ?: return

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}
