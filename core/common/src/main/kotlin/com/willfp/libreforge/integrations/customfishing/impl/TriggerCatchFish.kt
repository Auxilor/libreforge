package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent
import org.bukkit.event.EventHandler

object TriggerCatchFish : Trigger("catch_fish") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FishingLootSpawnEvent) {
        val player = event.player ?: return
        val loot = event.loot.id() ?: return
        val location = event.location ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                text = loot
            )
        )
    }
}
