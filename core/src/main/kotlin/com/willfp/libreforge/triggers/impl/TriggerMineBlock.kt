package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object TriggerMineBlock : Trigger("mine_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        this.dispatch(
            PlayerDispatcher(player),
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
