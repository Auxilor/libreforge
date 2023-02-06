package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

object TriggerTakeDamage : Trigger("take_damage") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val victim = event.entity

        if (victim !is Player) {
            return
        }

        this.dispatch(
            victim,
            TriggerData(
                player = victim,
                event = event,
                value = event.finalDamage
            )
        )
    }
}
