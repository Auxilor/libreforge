package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectSkillXpMultiplier : GenericMultiMultiplierEffect<Skill>(
    "skill_xp_multiplier",
    Skills::getByID,
    Skills::values,
    "skills"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        val multiplier = getMultiplier(player, event.skill)

        val wrapped = WrappedSkillXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
    }
}
