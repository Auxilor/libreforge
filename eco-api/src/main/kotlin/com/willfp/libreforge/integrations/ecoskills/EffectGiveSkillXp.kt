package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveSkillXp : Effect(
    "give_skill_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("skill", "You must specify the skill to give xp for!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        EcoSkillsAPI.getInstance().giveSkillExperience(
            player,
            Skills.getByID(config.getString("skill")) ?: Skills.COMBAT,
            config.getDoubleFromExpression("amount", player)
        )
    }
}
