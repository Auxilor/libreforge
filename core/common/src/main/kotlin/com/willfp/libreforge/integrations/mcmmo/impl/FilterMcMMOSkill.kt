package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.experience.McMMOPlayerExperienceEvent
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterMcMMOSkill : Filter<NoCompileData, Collection<String>>("skill") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event

        if (event is McMMOPlayerSkillEvent) {
            return value.any { skillName ->
                skillName.equals(event.skill.name, ignoreCase = true)
            }
        }

        if (event is McMMOPlayerAbilityEvent) {
            return value.any { skillName ->
                skillName.equals(event.skill.name, ignoreCase = true)
            }
        }

        if (event is McMMOPlayerExperienceEvent) {
            return value.any { skillName ->
                skillName.equals(event.skill.name, ignoreCase = true)
            }
        }

        return true
    }
}