package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerGainSkillXp : Trigger(
    "gain_skill_xp", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = WrappedSkillXpEvent(event)
            ),
            event.amount
        )
    }
}
