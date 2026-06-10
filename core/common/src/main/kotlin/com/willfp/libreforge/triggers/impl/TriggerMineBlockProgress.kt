package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDamageEvent

object TriggerMineBlockProgress : Trigger("mine_block_progress") {
    override val description = "Fires each time the player deals a hit of damage to a block while mining."

    override val categories = setOf("world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block being mined.",
        TriggerParameter.LOCATION to "The location of the block being mined.",
        TriggerParameter.ITEM to "The item in the player's main hand."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockDamageEvent) {
        val player = event.player
        val block = event.block

        this.dispatch(
            player.toDispatcher(),
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
