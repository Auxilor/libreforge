package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveXp : Effect(
    "give_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        if (Prerequisite.HAS_PAPER.isMet) {
            player.giveExp(config.getIntFromExpression("amount", player), config.getBool("apply_mending"))
        } else {
            player.giveExp(config.getIntFromExpression("amount", player))
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of xp to give!"
            )
        )

        if (Prerequisite.HAS_PAPER.isMet) {
            if (!config.has("apply_mending")) violations.add(
                ConfigViolation(
                    "apply_mending",
                    "You must specify whether or not to apply mending!"
                )
            )
        }

        return violations
    }
}