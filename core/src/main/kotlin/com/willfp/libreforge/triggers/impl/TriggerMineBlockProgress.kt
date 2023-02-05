package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedBlockBreakProgressEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDamageEvent

class TriggerMineBlockProgress : Trigger(
    "mine_block_progress", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockDamageEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        val block = event.block

        this.dispatch(
            player,
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = event,
                item = player.inventory.itemInMainHand
            )
        )
    }
}
