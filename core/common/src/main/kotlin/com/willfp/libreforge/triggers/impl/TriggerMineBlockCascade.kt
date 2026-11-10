package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.BlockCascades
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

object TriggerMineBlockCascade : Trigger("mine_block_cascade") {
    override val description = "Fires for each block broken in a cascading plant break, such as sugar cane, bamboo, kelp, vines, or chorus."

    override val categories = setOf("world")

    override val additionalInfo = listOf("This fires once for every block in the stack, not just the block the player broke.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The block in the cascade that was broken.",
        TriggerParameter.LOCATION to "The location of the block.",
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
        val source = event.block

        val blocks = BlockCascades.collect(source)

        if (blocks.isEmpty()) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, source)) {
            return
        }

        val item = player.inventory.itemInMainHand
        val dispatcher = player.toDispatcher()

        for (block in blocks) {
            this.dispatch(
                dispatcher,
                TriggerData(
                    player = player,
                    block = block,
                    location = block.location,
                    event = event,
                    item = item
                )
            )
        }
    }
}
