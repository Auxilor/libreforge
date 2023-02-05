package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityToggleGlideEvent

class TriggerDeployElytra : Trigger(
    "deploy_elytra", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityToggleGlideEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity as? Player ?: return

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
