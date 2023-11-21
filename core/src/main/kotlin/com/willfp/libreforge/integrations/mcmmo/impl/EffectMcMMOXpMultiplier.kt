package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.datatypes.skills.PrimarySkillType
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent
import com.gmail.nossr50.mcMMO
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.PlayerDispatcher
import org.bukkit.event.EventHandler

object EffectMcMMOXpMultiplier : MultiMultiplierEffect<PrimarySkillType>("mcmmo_xp_multiplier") {
    override val key = "skills"

    override fun getElement(key: String): PrimarySkillType? {
        return mcMMO.p.skillTools.matchSkill(key)
    }

    override fun getAllElements(): Collection<PrimarySkillType> {
        return PrimarySkillType.values().toList()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerXpGainEvent) {
        val player = event.player

        event.rawXpGained *= getMultiplier(PlayerDispatcher(player), event.skill).toFloat()
    }
}
