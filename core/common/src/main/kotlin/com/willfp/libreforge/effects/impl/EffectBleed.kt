package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.impl.TriggerKill


object EffectBleed : Effect<NoCompileData>("bleed") {
    override val description = "Deals damage to the victim repeatedly over a set number of ticks."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of bleed ticks!",
            description = "The number of times damage is dealt. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "3 + %level%"
        )
        require(
            "damage",
            "You must specify the amount of damage to deal!",
            description = "The damage dealt per interval. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.5"
        )
        require(
            "interval",
            "You must specify the tick delay between damages!",
            description = "The number of ticks between each damage application. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "true_damage",
            description = "If true, damage bypasses armor and resistance effects.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "use_source",
            description = "If true, the player is attributed as the damage source.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val damage = config.getDoubleFromExpression("damage", data)
        val interval = config.getIntFromExpression("interval", data)
        val amount = config.getIntFromExpression("amount", data)
        val trueDamage = config.getBool("true_damage")
        val source = if (config.getBool("use_source")) data.player else null

        var current = 0

        plugin.scheduler.on(victim).runTimer({ task ->
            current++

            val killed = damage >= victim.health

            if (killed) {
                if (Prerequisite.HAS_PAPER.isMet) {
                    victim.killer = data.player
                }

                if (data.player != null) {
                    TriggerKill.force(
                        data.player,
                        victim
                    )
                }
            }

            victim.dealDamage(damage, source, trueDamage)

            if (current >= amount || killed) {
                task.cancel()
            }
        }, interval.toLong(), interval.toLong())

        return true
    }
}
