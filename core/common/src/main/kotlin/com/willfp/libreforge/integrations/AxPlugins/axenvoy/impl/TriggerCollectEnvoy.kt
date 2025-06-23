package com.willfp.libreforge.integrations.AxPlugins.axenvoy.impl

import com.artillexstudios.axenvoy.event.EnvoyCrateCollectEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCollectEnvoy : Trigger("collect_envoy") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
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