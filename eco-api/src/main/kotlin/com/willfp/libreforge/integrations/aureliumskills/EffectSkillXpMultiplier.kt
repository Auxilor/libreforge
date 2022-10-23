package com.willfp.libreforge.integrations.aureliumskills

import com.archyx.aureliumskills.api.event.XpGainEvent
import com.archyx.aureliumskills.skills.Skill
import com.archyx.aureliumskills.skills.Skills
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectSkillXpMultiplier : GenericMultiMultiplierEffect<Skill>(
    "skill_xp_multiplier",
    { Skills.valueOf(it.uppercase()) },
    { Skills.values().toList() },
    "skills"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: XpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        val multiplier = getMultiplier(player, event.skill)

        event.amount *= multiplier
    }
}
