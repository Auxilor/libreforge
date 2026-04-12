package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler


object TriggerEntityItemDrop : Trigger("entity_item_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        if (Prerequisite.HAS_PAPER.isMet) {
            if (event.deathEvent.isCancelled) {
                return
            }
        }

        val entity = event.victim
        val killer = event.killer.tryAsLivingEntity() ?: return
        val originalDrops = event.drops.filterNotEmpty()

        val editableEvent = EditableDropEvent(
            initialDrops = originalDrops,
            cause = DropCause.ENTITY,
            context = DropContext(
                player = killer as? Player,
                entity = entity
            ),
            dropLocation = entity.location,
            cancellable = event.deathEvent as? Cancellable
        )

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer as? Player,
                victim = entity,
                location = entity.location,
                event = editableEvent,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        event.drops.clear()
        event.drops.addAll(editableEvent.drops)

        if (killer is Player) {
            val totalXP = editableEvent.items.sumOf { it.xp }
            if (totalXP > 0) {
                DropQueue(killer)
                    .setLocation(entity.location)
                    .addXP(totalXP)
                    .push()
            }
        }
    }
}
