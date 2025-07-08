package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent
import org.bukkit.event.EventHandler

object TriggerLevelUpSkill : Trigger("level_up_skill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: SkillLevelUpEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.level.toDouble(),
                text = event.skill.name().lowercase()
            )
        )
    }
}