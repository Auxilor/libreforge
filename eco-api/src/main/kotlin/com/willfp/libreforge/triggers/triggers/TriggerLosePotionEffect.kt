package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent

class TriggerLosePotionEffect : Trigger(
    "lose_potion_effect", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        if (event.oldEffect == null) {
            return
        }

        if (event.newEffect != null) {
            return
        }

        val player = event.entity as? Player ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = GenericCancellableEvent(event)
            )
        )
    }
}
