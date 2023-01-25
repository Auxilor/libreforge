package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveXp : Effect(
    "give_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        if (Prerequisite.HAS_PAPER.isMet) {
            player.giveExp(config.getIntFromExpression("amount", data), config.getBoolOrNull("apply_mending") ?: true)
        } else {
            player.giveExp(config.getIntFromExpression("amount", data))
        }
    }
}
