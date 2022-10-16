package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterSkill : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val skill = (data.event as? WrappedSkillXpEvent)?.skill
            ?: (data.event as? WrappedSkillLevelUpEvent)?.skill
            ?: return true

        return config.withInverse("skill", Config::getStrings) {
            it.any { skillName ->
                skillName.equals(skill.id, ignoreCase = true)
            }
        }
    }
}
