package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent

object TriggerLosePotionEffect : Trigger("lose_potion_effect") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        if (event.oldEffect == null) {
            return
        }

        if (event.newEffect != null) {
            return
        }

        val player = event.entity as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
