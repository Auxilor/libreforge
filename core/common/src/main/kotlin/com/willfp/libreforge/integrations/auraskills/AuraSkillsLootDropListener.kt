package com.willfp.libreforge.integrations.auraskills

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import dev.aurelium.auraskills.api.event.loot.LootDropEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

private val MINING_CAUSES = setOf(
    LootDropEvent.Cause.MINING_LUCK,
    LootDropEvent.Cause.MINING_OTHER_LOOT
)

object AuraSkillsLootDropListener : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: LootDropEvent) {
        if (event.cause !in MINING_CAUSES) return
        if (event.entity != null) return

        val originalItem = event.item
        if (originalItem.type == Material.AIR || originalItem.amount <= 0) return

        val editableEvent = EditableDropEvent(
            initialDrops = listOf(originalItem),
            cause = DropCause.BLOCK,
            context = DropContext(
                player = event.player,
                block = null,
                tool = event.player.inventory.itemInMainHand
            ),
            dropLocation = event.location,
            cancellable = event
        )

        TriggerBlockItemDrop.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                block = null,
                location = event.location,
                event = editableEvent,
                item = null,
                value = originalItem.amount.toDouble()
            )
        )

        val dropResults = editableEvent.items

        if (editableEvent.drops.isEmpty()) {
            event.isCancelled = true
            return
        }

        event.item = editableEvent.drops.first()

        val totalXP = dropResults.sumOf { it.xp }
        if (totalXP > 0) {
            DropQueue(event.player)
                .setLocation(event.location)
                .addXP(totalXP)
                .push()
        }
    }
}
