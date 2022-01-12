package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveSkillXp : Effect(
    "give_skill_xp",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        EcoSkillsAPI.getInstance().giveSkillExperience(
            player,
            Skills.getByID(config.getString("skill")) ?: Skills.COMBAT,
            config.getDoubleFromExpression("amount", player)
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of xp to give!"
            )
        )

        config.getStringOrNull("skill")
            ?: violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the skill to give xp for!"
                )
            )

        return violations
    }
}