package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.libreforge.drops.LibreforgeDrops
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
    override val description = "Fires when the player kills an entity and the entity drops items."

    override val categories = setOf("entity", "combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was killed and dropped items.",
        TriggerParameter.ITEM to "The first item from the drop pool.",
        TriggerParameter.LOCATION to "The location of the killed entity.",
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
    fun handle(event: EntityDeathByEntityEvent) {
        if (Prerequisite.HAS_PAPER.isMet) {
            if (event.deathEvent.isCancelled) {
                return
            }
        }

        val entity = event.victim
        val killer = event.killer.tryAsLivingEntity() ?: return

        val context = DropContext(
            player = killer as? Player,
            entity = entity
        )

        val contributed = LibreforgeDrops.contributions(DropCause.ENTITY, context, entity.location)
        val originalDrops = event.drops.filterNotEmpty() + contributed.items

        val editableEvent = EditableDropEvent(
            initialDrops = originalDrops,
            cause = DropCause.ENTITY,
            context = context,
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

        // Contributed xp joins the death event's own xp, so that later handlers
        // (telekinesis at HIGH, which reads the live value) pick it up too.
        if (contributed.xp > 0) {
            event.deathEvent.droppedExp += contributed.xp
        }

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
