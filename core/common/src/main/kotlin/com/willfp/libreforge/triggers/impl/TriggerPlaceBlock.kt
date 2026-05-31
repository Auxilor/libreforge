package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

object TriggerPlaceBlock : Trigger("place_block") {
    override val description = "Fires when the player places a block."

    override val categories = setOf("world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block that was placed.",
        TriggerParameter.LOCATION to "The location where the block was placed.",
        TriggerParameter.ITEM to "The item that was placed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.blockPlaced

        if (!AntigriefManager.canPlaceBlock(player, block)) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = event,
                item = event.itemInHand
            )
        )
    }
}
