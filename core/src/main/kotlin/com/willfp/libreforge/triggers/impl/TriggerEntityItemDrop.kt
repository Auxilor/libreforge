package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.util.tryAsPlayer
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedEntityDropEvent
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
        val originalDrops = event.drops.filterNot { it.isEmpty }

        val wrapped = WrappedEntityDropEvent(event.deathEvent)

        this.dispatch(
            player,
            TriggerData(
                player = player,
                victim = entity,
                location = entity.location,
                event = wrapped,
                value = originalDrops.sumOf { it.amount }.toDouble()
            )
        )

        val newDrops = originalDrops.map(wrapped::modify)
        var xp = 0
        for ((_, i) in newDrops) {
            xp += i
        }

        event.drops.clear()
        event.drops.addAll(newDrops.map { it.first })

        if (xp > 0) {
            DropQueue(player)
                .setLocation(entity.location)
                .addXP(xp)
                .push()
        }
    }
}
