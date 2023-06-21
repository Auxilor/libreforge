package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.impl.TriggerKill


object EffectBleed : Effect<NoCompileData>("bleed") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of bleed ticks!")
        require("damage", "You must specify the amount of damage to deal!")
        require("interval", "You must specify the tick delay between damages!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val damage = config.getDoubleFromExpression("damage", data)
        val interval = config.getIntFromExpression("interval", data)
        val amount = config.getIntFromExpression("amount", data)

        var current = 0

        plugin.runnableFactory.create {
            current++

            if (damage >= victim.health) {
                victim.killer = data.player

                if (data.player != null) {
                    TriggerKill.force(
                        data.player,
                        victim
                    )
                }
            }

            victim.damage(damage)

            if (current >= amount) {
                it.cancel()
            }
        }.runTaskTimer(interval.toLong(), interval.toLong())

        return true
    }
}
