package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Container
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent

object TriggerBlockItemDrop : Trigger("block_item_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.LOW
    )
    fun handle(event: BlockDropItemEvent) {
        val player = event.player
        val block = event.block

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (event.blockState is Container) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val brokenBlock = BrokenBlock(block, event.blockState.type, event.blockState.blockData)

        val itemEntityToStack = event.items.associateWith { it.itemStack }
        val originalDrops = itemEntityToStack.values.toList().filterNotEmpty()

        val editableEvent = EditableDropEvent(
            initialDrops = originalDrops,
            cause = DropCause.BLOCK,
            context = DropContext(
                player = player,
                block = brokenBlock,
                tool = player.inventory.itemInMainHand
            ),
            dropLocation = block.location,
            cancellable = event
        )

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = brokenBlock,
                location = block.location,
                event = editableEvent,
                item = null,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        val dropResults = editableEvent.items

        val remainingDrops = editableEvent.drops
        event.items.removeIf { item ->
            val stack = itemEntityToStack[item] ?: return@removeIf true
            remainingDrops.none { drop -> drop === stack }
        }

        for (item in event.items) {
            val stack = itemEntityToStack[item] ?: continue
            item.setItemStack(stack)
        }

        val totalXP = dropResults.sumOf { it.xp }
        if (totalXP > 0) {
            DropQueue(player)
                .setLocation(block.location)
                .addXP(totalXP)
                .push()
        }
    }

    private class BrokenBlock(
        private val block: Block,
        private val type: Material,
        private val data: BlockData
    ): Block by block {
        override fun getType() = type
        override fun getBlockData(): BlockData = data
    }
}
