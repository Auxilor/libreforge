package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.integrations.mythicmobs.utils.isMythicMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerTakeDamage : Trigger("take_damage") {
    override val description = "Fires when the player takes any damage."

    override val categories = setOf("combat")

    override val additionalInfo = listOf("Does not fire for void, /kill, or suicide damage.",)

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that took the damage.",
        TriggerParameter.VALUE to "The final damage taken."
    )

    var blockEntityDamageByEntity: Boolean = false

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    private val ignoredCauses = setOf(
        EntityDamageEvent.DamageCause.VOID,
        EntityDamageEvent.DamageCause.SUICIDE,
        EntityDamageEvent.DamageCause.KILL
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (event.cause in ignoredCauses) return
        // If Damager (or Projectile Shooter) is MythicMob, then skip. Use 'take_mythic_damage' instead.
        if (event is EntityDamageByEntityEvent && event.damager.tryAsLivingEntity()?.isMythicMob() == true) return
        if (event is EntityDamageByEntityEvent) return

        val victim = event.entity
        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = victim as? LivingEntity,
                event = event,
                value = event.finalDamage
            )
        )
    }
}