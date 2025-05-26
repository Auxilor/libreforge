package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.MemberRoleChangeEvent
import org.bukkit.event.EventHandler

object TriggerChangeTownRole : Trigger("change_town_role") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MemberRoleChangeEvent) {
        val player = event.player.player ?: return
        val role = event.newRole.name ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = role
            )
        )
    }
}