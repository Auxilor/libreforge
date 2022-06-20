package com.willfp.libreforge.integrations.ecoskills

import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.skills.Skill
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedSkillLevelUpEvent(
    private val event: PlayerSkillLevelUpEvent
) : WrappedEvent<PlayerSkillLevelUpEvent> {
    val skill: Skill
        get() = event.skill
}
