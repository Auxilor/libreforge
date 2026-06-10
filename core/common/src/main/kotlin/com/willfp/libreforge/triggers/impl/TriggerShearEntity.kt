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
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerShearEntityEvent

@Deprecated("Use 'shear' instead")
object TriggerShearEntity : Trigger("shear_entity") {
    override val description = "Fires when the player shears a living entity."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf(
        "Deprecated — use 'shear' instead, which also handles shearing blocks."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was sheared.",
        TriggerParameter.ITEM to "The shears item used.",
        TriggerParameter.LOCATION to "The location of the sheared entity.",
        TriggerParameter.VALUE to "The number of items dropped."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
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
}
