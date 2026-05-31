package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import io.papermc.paper.event.block.PlayerShearBlockEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerShearEntityEvent

object TriggerShear : Trigger("shear") {
    override val description = "Fires when the player shears an entity or a block."

    override val categories = setOf("interaction")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was sheared, if shearing an entity.",
        TriggerParameter.BLOCK to "The block that was sheared, if shearing a block.",
        TriggerParameter.ITEM to "The shears item used.",
        TriggerParameter.LOCATION to "The location of the entity or block that was sheared."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerShearEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return

        val originalDrops = event.drops.filterNotEmpty()

        if (originalDrops.isEmpty()) {
            return
        }

        val editableEvent = EditableDropEvent(
            initialDrops = originalDrops,
            cause = DropCause.SHEAR,
            context = DropContext(
                player = event.player,
                entity = entity,
                tool = event.item
            ),
            dropLocation = entity.location,
            cancellable = event
        )

        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                victim = entity,
                item = event.item,
                location = entity.location,
                event = editableEvent,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        val totalXP = editableEvent.items.sumOf { it.xp }
        if (totalXP > 0) {
            DropQueue(event.player)
                .setLocation(entity.location)
                .addXP(totalXP)
                .push()
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerShearBlockEvent) {
        val block = event.block
        val originalDrops = event.drops.filterNotEmpty()

        if (originalDrops.isEmpty()) {
            return
        }

        val editableEvent = EditableDropEvent(
            initialDrops = originalDrops,
            cause = DropCause.BLOCK,
            context = DropContext(
                player = event.player,
                block = block,
                tool = event.item
            ),
            dropLocation = block.location,
            cancellable = event
        )

        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                block = block,
                item = event.item,
                location = block.location,
                event = editableEvent,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        event.drops.clear()
        event.drops.addAll(editableEvent.drops)

        val totalXP = editableEvent.items.sumOf { it.xp }
        if (totalXP > 0) {
            DropQueue(event.player)
                .setLocation(block.location)
                .addXP(totalXP)
                .push()
        }
    }
}

