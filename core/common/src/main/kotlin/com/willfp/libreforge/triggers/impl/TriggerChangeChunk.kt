package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

object TriggerChangeChunk : Trigger("change_chunk") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityMoveEvent) {
        val entity = event.entity as? LivingEntity ?: return

        if (entity is Player) {
            return
        }

        if (!event.hasExplicitlyChangedBlock()) {
            return
        }

        if (event.to.chunk.chunkKey != event.from.chunk.chunkKey) {
            return
        }

        this.dispatch(
            entity.toDispatcher(), TriggerData(
                victim = entity as? LivingEntity,
                location = event.to,
                velocity = entity.velocity,
                event = event,
                item = entity.equipment?.itemInMainHand
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (Prerequisite.HAS_PAPER.isMet) {
            if (!event.hasChangedBlock()) {
                return
            }
        }

        if (event.to.chunk.chunkKey != event.from.chunk.chunkKey) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                velocity = player.velocity,
                event = event,
                item = player.equipment.itemInMainHand,
            )
        )
    }
}
