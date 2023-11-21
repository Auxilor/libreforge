package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.tryAsPlayer
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableEntityDropEvent
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
        val player = event.killer.tryAsPlayer() ?: return
        val originalDrops = event.drops.filterNotEmpty()

        val editableEvent = EditableEntityDropEvent(event.deathEvent)

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                victim = entity,
                location = entity.location,
                event = editableEvent,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        val newDrops = editableEvent.items

        event.drops.clear()
        event.drops.addAll(newDrops.map { it.item })

        if (newDrops.sumOf { it.xp } > 0) {
            DropQueue(player)
                .setLocation(entity.location)
                .addXP(newDrops.sumOf { it.xp })
                .push()
        }
    }
}
