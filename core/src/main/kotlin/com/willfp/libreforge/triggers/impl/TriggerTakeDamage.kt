package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

object TriggerTakeDamage : Trigger("take_damage") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    private val ignoredCauses = mutableSetOf(
        EntityDamageEvent.DamageCause.VOID,
        EntityDamageEvent.DamageCause.SUICIDE
    )

    init {
        if (Prerequisite.HAS_1_20.isMet) {
            ignoredCauses += EntityDamageEvent.DamageCause.KILL
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val victim = event.entity

        if (event.cause in ignoredCauses) {
            return
        }

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
