package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent
import dev.aurelium.auraskills.api.event.skill.XpGainEvent
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.skill.Skill

object FilterSkill : Filter<NoCompileData, Collection<Skill>>("skill") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<Skill> {
        return config.getStrings(key).mapNotNull { skillId ->
            try {
                AuraSkillsApi.get().globalRegistry.getSkill(NamespacedId.fromDefault(skillId))
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    override fun isMet(data: TriggerData, value: Collection<Skill>, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is SkillLevelUpEvent -> {
                value.any { it == event.skill }
            }
            is XpGainEvent -> {
                value.any { it == event.skill }
            }
            else -> true
        }
    }
}