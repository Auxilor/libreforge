package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers


class EffectBleed : Effect(
    "bleed",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of bleed ticks!")
        require("damage", "You must specify the amount of damage to deal!")
        require("interval", "You must specify the tick delay between damages!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return

        val damage = config.getDoubleFromExpression("damage", data)
        val interval = config.getIntFromExpression("interval", data)
        val amount = config.getIntFromExpression("amount", data)

        var current = 0

        this.plugin.runnableFactory.create {
            current++

            if (damage >= victim.health) {
                victim.killer = data.player
            }

            victim.damage(damage)

            if (current >= amount) {
                it.cancel()
            }
        }.runTaskTimer(interval.toLong(), interval.toLong())
    }
}
