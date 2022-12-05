package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetEvent

class TriggerEntityTarget : Trigger(
    "entity_target", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityTargetEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.target as? Player ?: return
        val entity = event.entity as? LivingEntity ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                victim = entity
            )
        )
    }
}
