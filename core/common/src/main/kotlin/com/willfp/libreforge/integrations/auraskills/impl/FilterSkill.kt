package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent
import dev.aurelium.auraskills.api.event.skill.XpGainEvent

object FilterSkill : Filter<NoCompileData, Collection<String>>("skill") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is SkillLevelUpEvent -> {
                value.any { it.equals(event.skill.id.toString(), ignoreCase = true) }
            }

            is XpGainEvent -> {
                value.any { it.equals(event.skill.id.toString(), ignoreCase = true) }
            }

            else -> true
        }
    }
}