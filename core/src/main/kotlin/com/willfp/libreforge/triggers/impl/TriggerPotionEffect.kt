package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent

object TriggerPotionEffect : Trigger("potion_effect") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        if (event.newEffect == null) {
            return
        }

        val player = event.entity as? Player ?: return

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                text = event.newEffect?.type?.name?.lowercase()
            )
        )
    }
}
