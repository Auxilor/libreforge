package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.util.UUID

object TriggerMeleeAttack : Trigger("melee_attack") {
    override val description = "Fires when the player lands a melee hit on an entity."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was hit.",
        TriggerParameter.LOCATION to "The victim's location at the time of the hit.",
        TriggerParameter.ITEM to "The item in the attacker's main hand."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    private val processedEvents = mutableSetOf<UUID>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? LivingEntity ?: return
        val victim = event.entity as? LivingEntity ?: return

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        if (processedEvents.contains(event.entity.uniqueId)) {
            return
        }

        processedEvents.add(event.entity.uniqueId)

        this.dispatch(
            attacker.toDispatcher(),
            TriggerData(
                player = attacker as? Player,
                victim = victim,
                location = victim.location,
                event = event,
                item = attacker.equipment?.itemInMainHand,
                value = event.finalDamage
            )
        )

        processedEvents.clear()
    }
}
