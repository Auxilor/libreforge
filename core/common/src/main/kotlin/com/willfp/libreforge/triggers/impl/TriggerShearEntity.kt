package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.proxy.renamedValues
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerShearEntityEvent

object TriggerShearEntity : Trigger("shear_entity") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerShearEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return

        if (entity.type !in listOf(EntityType.SHEEP, renamedValues().entities.mooshroom)) {
            return
        }

        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                victim = entity,
                location = entity.location,
                event = event,
            )
        )
    }
}
