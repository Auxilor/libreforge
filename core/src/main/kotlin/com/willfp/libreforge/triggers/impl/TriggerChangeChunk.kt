package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

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
        val entity = event.entity

        if (event.to.chunk.x != event.from.chunk.x
            || event.to.chunk.z != event.from.chunk.z
        ) {
            return
        }

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                player = entity as? Player,
                victim = entity as? LivingEntity,
                location = event.to,
                velocity = entity.velocity,
                event = event,
                item = entity.equipment?.itemInMainHand
            )
        )
    }
}
