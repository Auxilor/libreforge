package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

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

        if (entity is Player) {
            return
        }

        if (!event.hasChangedBlock()) {
            return
        }

        val distance = if (event.to.world == event.from.world) {
            event.to.distance(event.from)
        } else {
            0.0
        }

        val runnable = Runnable {
            this.dispatch(
                entity.toDispatcher(),
                TriggerData(
                    location = entity.location,
                    velocity = entity.velocity,
                    event = event,
                    item = entity.equipment?.itemInMainHand,
                    value = distance
                )
            )
        }

        if (Prerequisite.HAS_FOLIA.isMet) {
            if (entity.isValid) // folia issue, sometimes entity moves when is dead, making the task impossible
                plugin.scheduler.runTask(entity, runnable)
        } else
            runnable.run()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

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

        val runnable = Runnable {
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

        if (Prerequisite.HAS_FOLIA.isMet) {
            if (player.isValid) // folia issue, sometimes player moves when is dead, making the task impossible
                plugin.scheduler.runTask(player, runnable)
        } else
            runnable.run()
    }
}
