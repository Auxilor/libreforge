package com.willfp.libreforge.integrations.customfishing.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler

object TriggerCatchFish : Trigger("catch_fish") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FishingLootSpawnEvent) {
        val player = event.player ?: return
        val loot = event.loot.id() ?: return
        val location = event.location ?: return
        val entity = event.entity
        val itemStack = (entity as? Item)?.itemStack

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                text = loot,
                item = itemStack
            )
        )
    }
}