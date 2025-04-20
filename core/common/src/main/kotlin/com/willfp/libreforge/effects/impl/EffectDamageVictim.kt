package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter


object EffectDamageVictim : Effect<NoCompileData>("damage_victim") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
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

        return true
    }
}
