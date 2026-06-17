package com.willfp.libreforge.integrations.custom_blocks.nexo.impl

import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent
import com.nexomc.nexo.utils.drops.Drop
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object TriggerNexoBlockItemDrop : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: NexoBlockBreakEvent) {
        if (!TriggerBlockItemDrop.isEnabled) return

        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
        if (!AntigriefManager.canBreakBlock(player, event.block)) return

        val items = event.drop.loots.mapNotNull { it.itemStack() }.filterNotEmpty()
        if (items.isEmpty()) return

        val block = event.block

        val editableEvent = EditableDropEvent(
            initialDrops = items,
            cause = DropCause.BLOCK,
            context = DropContext(
                player = player,
                block = block,
                tool = player.inventory.itemInMainHand
            ),
            dropLocation = block.location
        )

        TriggerBlockItemDrop.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = editableEvent,
                item = null,
                value = items.sumOf { it.amount.toDouble() }
            )
        )

        event.drop = Drop.emptyDrop()

        val remaining = editableEvent.drops
        if (remaining.isEmpty()) return

        if (TelekinesisUtils.testPlayer(player)) {
            DropQueue(player)
                .setLocation(block.location)
                .addItems(remaining)
                .forceTelekinesis()
                .push()
        } else {
            remaining.forEach { block.world.dropItemNaturally(block.location, it) }
        }
    }
}
