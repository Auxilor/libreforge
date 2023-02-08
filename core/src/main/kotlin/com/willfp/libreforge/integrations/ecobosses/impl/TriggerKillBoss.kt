package com.willfp.libreforge.integrations.ecobosses.impl

import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler

object TriggerKillBoss : Trigger("kill_boss") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossKillEvent) {
        val killer = event.killer ?: return
        val entity = event.boss.entity ?: return

        this.dispatch(
            killer,
            TriggerData(
                player = killer,
                victim = entity,
                location = entity.location,
                value = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            )
        )
    }
}
