package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

class TriggerPlaceBlock : Trigger(
    "place_block", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockPlaceEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        val block = event.blockPlaced

        if (!AntigriefManager.canPlaceBlock(player, block)) {
            return
        }

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = GenericCancellableEvent(event),
                item = event.itemInHand
            )
        )
    }
}
