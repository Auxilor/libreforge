package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers


class EffectDamageVictim : Effect(
    "damage_victim",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val player = data.player

        val damage = config.getDoubleFromExpression("damage", data)

        if (config.getBool("true_damage")) {
            if (damage >= victim.health) {
                victim.health = 0.0
                if (Prerequisite.HAS_PAPER.isMet) {
                    victim.killer = player
                }
            } else {
                victim.health -= damage
            }
        } else {
            if (config.getBool("use_source")) {
                victim.damage(damage, player)
            } else {
                victim.damage(damage)
            }
        }
    }
}
