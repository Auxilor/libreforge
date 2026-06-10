package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPotionEffectEvent

object TriggerPotionEffect : Trigger("potion_effect") {
    override val description = "Fires when an entity receives a potion effect."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that received the effect.",
        TriggerParameter.LOCATION to "The entity's location.",
        TriggerParameter.TEXT to "The namespaced key of the potion effect type (e.g. speed, strength)."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        if (event.newEffect == null) {
            return
        }

        val entity = event.entity as? LivingEntity ?: return

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                player = entity as? Player,
                victim = entity,
                location = entity.location,
                event = event,
                text = event.newEffect?.type?.key?.key?.lowercase()
            )
        )
    }
}
