package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent

object TriggerLosePotionEffect : Trigger("lose_potion_effect") {
    override val description = "Fires when an entity loses a potion effect."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that lost the potion effect.",
        TriggerParameter.LOCATION to "The entity's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        if (event.oldEffect == null) {
            return
        }

        if (event.newEffect != null) {
            return
        }

        val entity = event.entity as? LivingEntity ?: return

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                player = entity as? Player,
                victim = entity,
                location = entity.location,
                event = event
            )
        )
    }
}
