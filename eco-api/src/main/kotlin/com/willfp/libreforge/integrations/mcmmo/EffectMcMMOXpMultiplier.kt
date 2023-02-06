package com.willfp.libreforge.integrations.mcmmo

import com.gmail.nossr50.datatypes.skills.PrimarySkillType
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent
import com.gmail.nossr50.mcMMO
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectMcMMOXpMultiplier : GenericMultiMultiplierEffect<PrimarySkillType>(
    "mcmmo_xp_multiplier",
    { mcMMO.p.skillTools.matchSkill(it) },
    { PrimarySkillType.values().toList() },
    "skills"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerXpGainEvent) {
        val player = event.player

        val multiplier = getMultiplier(player, event.skill)

        event.rawXpGained = (event.rawXpGained * multiplier).toFloat()
    }
}
