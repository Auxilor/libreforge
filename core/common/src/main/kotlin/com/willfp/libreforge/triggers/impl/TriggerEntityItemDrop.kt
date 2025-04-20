package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableEntityDropEvent
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler


object TriggerEntityItemDrop : Trigger("entity_item_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
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

        val editableEvent = EditableEntityDropEvent(event.deathEvent)

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

        val newDrops = editableEvent.items

        event.drops.clear()
        event.drops.addAll(newDrops.map { it.item })

        if (killer is Player) {
            if (newDrops.sumOf { it.xp } > 0) {
                DropQueue(killer)
                    .setLocation(entity.location)
                    .addXP(newDrops.sumOf { it.xp })
                    .push()
            }
        }
    }
}
