package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerLevelUpSkill : Trigger(
    "level_up_skill", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = WrappedSkillLevelUpEvent(event)
            ),
            event.level.toDouble()
        )
    }
}
