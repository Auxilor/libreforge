package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler

class TriggerKillBoss : Trigger(
    "kill_boss", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossKillEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val killer = event.killer ?: return
        val entity = event.boss.entity ?: return

        this.processTrigger(
            killer,
            TriggerData(
                player = killer,
                victim = entity,
                location = entity.location
            ),
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        )
    }
}
