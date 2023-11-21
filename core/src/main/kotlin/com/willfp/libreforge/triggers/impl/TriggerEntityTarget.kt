package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetEvent

object TriggerEntityTarget : Trigger("entity_target") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityTargetEvent) {
        val player = event.target as? Player ?: return
        val entity = event.entity as? LivingEntity ?: return

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                victim = entity
            )
        )
    }
}
