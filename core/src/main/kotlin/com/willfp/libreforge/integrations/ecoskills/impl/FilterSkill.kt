package com.willfp.libreforge.integrations.ecoskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.SkillEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSkill : Filter<NoCompileData, Collection<String>>("skill") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun filter(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? SkillEvent ?: return true

        return value.any { skillName ->
            skillName.equals(event.skill.id, ignoreCase = true)
        }
    }
}
