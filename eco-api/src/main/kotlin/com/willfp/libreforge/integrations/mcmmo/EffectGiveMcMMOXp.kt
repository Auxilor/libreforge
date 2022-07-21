package com.willfp.libreforge.integrations.mcmmo

import com.gmail.nossr50.api.ExperienceAPI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveMcMMOXp : Effect(
    "give_mcmmo_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        ExperienceAPI.addRawXP(
            player,
            config.getString("skill"),
            config.getDoubleFromExpression("amount", player).toFloat(),
            "UNKNOWN"
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

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the skill to give xp for!"
            )
        )

        return violations
    }
}