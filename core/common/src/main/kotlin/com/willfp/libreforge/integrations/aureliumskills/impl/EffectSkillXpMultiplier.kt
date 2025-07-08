package com.willfp.libreforge.integrations.aureliumskills.impl

import com.archyx.aureliumskills.api.event.XpGainEvent
import com.archyx.aureliumskills.skills.Skill
import com.archyx.aureliumskills.skills.Skills
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

@Deprecated("AureliumSkills integration is deprecated and will be removed in the future. Update to AuraSkills instead!")
object EffectSkillXpMultiplier : MultiMultiplierEffect<Skill>("skill_xp_multiplier") {
    override val key = "skills"

    override fun getElement(key: String): Skill {
        return Skills.valueOf(key.uppercase())
    }

    override fun getAllElements(): Collection<Skill> {
        return Skills.values().toList()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: XpGainEvent) {
        val player = event.player

        val multiplier = getMultiplier(player.toDispatcher(), event.skill)

        event.amount *= multiplier
    }
}
