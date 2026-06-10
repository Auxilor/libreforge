package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableXpDropEvent
import com.willfp.libreforge.triggers.event.XpDropCause
import com.willfp.libreforge.triggers.event.XpDropContext
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerEntityXpDrop : Trigger("entity_xp_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.LOW
    )
    fun handle(event: EntityDeathByEntityEvent) {
        if (Prerequisite.HAS_PAPER.isMet) {
            if (event.deathEvent.isCancelled) {
                return
            }
        }

        val entity = event.victim
        val killer = event.killer.tryAsLivingEntity() ?: return

        if (event.deathEvent.droppedExp <= 0) {
            return
        }

        val editableEvent = EditableXpDropEvent(
            initialXp = event.deathEvent.droppedExp,
            cause = XpDropCause.ENTITY,
            context = XpDropContext(
                player = killer as? Player,
                entity = entity
            ),
            dropLocation = entity.location
        )

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer as? Player,
                victim = entity,
                location = entity.location,
                event = editableEvent,
                value = event.deathEvent.droppedExp.toDouble()
            )
        )

        event.deathEvent.droppedExp = editableEvent.finalXp
    }
}
