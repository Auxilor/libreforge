package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.internal.api.AsyncEntityMoveEvent
import com.willfp.libreforge.internal.api.AsyncPlayerMoveEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
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
    fun handle(event: AsyncEntityMoveEvent) {
        val entity = event.entity

        if (entity is Player) {
            return
        }

        if (!event.hasExplicitlyChangedBlock()) {
            return
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

    @EventHandler(ignoreCancelled = true)
    fun handle(event: AsyncPlayerMoveEvent) {
        val player = event.player

        if (Prerequisite.HAS_PAPER.isMet) {
            if (!event.hasExplicitlyChangedBlock()) {
                return
            }
        }

        val distance = if (event.to.world == event.from.world) {
            event.to.distance(event.from)
        } else {
            0.0
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                velocity = player.velocity,
                event = event,
                item = player.equipment.itemInMainHand,
                value = distance
            )
        )
    }
}
