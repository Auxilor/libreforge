package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerMove : Trigger("move") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityMoveEvent) {
        val entity = event.entity

        if (Prerequisite.HAS_PAPER.isMet) {
            if (!event.hasChangedBlock()) {
                return
            }
        }

        val distance = if (event.to.world == event.from.world) {
            event.to.distance(event.from)
        } else {
            0.0
        }

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                player = entity as? Player,
                location = entity.location,
                velocity = entity.velocity,
                event = event,
                item = entity.equipment?.itemInMainHand,
                value = distance
            )
        )
    }
}
