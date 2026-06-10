package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

object TriggerMineBlock : Trigger("mine_block") {
    override val description = "Fires when the player successfully breaks a block."

    override val categories = setOf("world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block that was broken.",
        TriggerParameter.LOCATION to "The location of the broken block.",
        TriggerParameter.ITEM to "The item in the player's main hand."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

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
