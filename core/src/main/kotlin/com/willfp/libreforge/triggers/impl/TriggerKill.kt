package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.tryAsPlayer
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler

object TriggerKill : Trigger("kill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        val killer = event.killer.tryAsPlayer() ?: return

        val victim = event.victim

        this.dispatch(
            killer,
            TriggerData(
                player = killer,
                victim = victim,
                location = victim.location,
                value = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            )
        )
    }
}
