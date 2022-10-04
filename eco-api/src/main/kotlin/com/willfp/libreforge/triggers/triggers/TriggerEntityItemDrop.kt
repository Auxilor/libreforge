package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.tryAsPlayer
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedEntityDropEvent
import org.bukkit.event.EventHandler


class TriggerEntityItemDrop : Trigger(
    "entity_item_drop", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (Prerequisite.HAS_PAPER.isMet) {
            if (event.deathEvent.isCancelled) {
                return
            }
        }

        val entity = event.victim
        val player = event.killer.tryAsPlayer() ?: return
        val originalDrops = event.drops

        val wrapped = WrappedEntityDropEvent(event.deathEvent)

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                victim = entity,
                location = entity.location,
                event = wrapped
            ),
            originalDrops.sumOf { it.amount }.toDouble()
        )

        val newDrops = originalDrops.map(wrapped::modify)
        var xp = 0
        for ((_, i) in newDrops) {
            xp += i
        }

        for (i in event.drops.indices) {
            event.drops[i] = newDrops[i].first
        }

        if (xp > 0) {
            DropQueue(player)
                .setLocation(entity.location)
                .addXP(xp)
                .push()
        }
    }
}
