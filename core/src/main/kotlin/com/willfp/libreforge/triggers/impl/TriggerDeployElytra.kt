package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityToggleGlideEvent

object TriggerDeployElytra : Trigger("deploy_elytra") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityToggleGlideEvent) {
        val player = event.entity as? Player ?: return

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
