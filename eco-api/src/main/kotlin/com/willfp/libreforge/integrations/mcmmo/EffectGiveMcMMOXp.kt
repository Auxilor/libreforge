package com.willfp.libreforge.integrations.mcmmo

import com.gmail.nossr50.api.ExperienceAPI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
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
    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("skill", "You must specify the skill to give xp for!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        ExperienceAPI.addRawXP(
            player,
            config.getString("skill"),
            config.getDoubleFromExpression("amount", player).toFloat(),
            "UNKNOWN"
        )
    }
}
