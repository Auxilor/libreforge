package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

object TriggerMoveToBlock : Trigger("move_to_block") {
    override val description = "Fires when the player moves to a new block, with the block they are now on."

    override val categories = setOf("movement", "world")

    override val additionalInfo = listOf(
        "The block is the one at the player's feet when it is a liquid or has collision (water, lava, carpet, slab), otherwise the block below.",
        "Use the blocks filter to choose which blocks fire it."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location that was moved to.",
        TriggerParameter.BLOCK to "The block the player is now on."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        if (!TriggerMove.hasChangedBlock(event)) {
            return
        }

        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = event.to,
                block = blockMovedTo(event.to.block)
            )
        )
    }

    private fun blockMovedTo(feet: Block): Block =
        if (feet.isLiquid || !feet.isPassable) feet else feet.getRelative(BlockFace.DOWN)
}
