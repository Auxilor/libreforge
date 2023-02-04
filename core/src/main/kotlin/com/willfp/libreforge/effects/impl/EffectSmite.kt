package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.LightningUtils
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSmite : Effect(
    "smite",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("damage", "You must specify the damage to deal!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val silent = config.getBool("silent")
        val damage = config.getDoubleFromExpression("damage", data.player)

        LightningUtils.strike(victim, damage, silent)
    }
}
