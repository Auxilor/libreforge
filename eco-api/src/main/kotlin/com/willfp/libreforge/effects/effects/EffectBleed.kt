package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Arrow
import org.bukkit.util.Vector
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class EffectBleed : Effect(
    "bleed",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val victim = data.victim ?: return

        val damage = config.getDouble("damage")
        val interval = config.getInt("interval")
        val amount = config.getInt("amount")

        var current = 0

        this.plugin.runnableFactory.create {
            current++
            victim.damage(damage)

            if (current >= amount) {
                it.cancel()
            }
        }.runTaskTimer(interval.toLong(), interval.toLong())
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getIntOrNull("amount")
            ?: violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the amount of bleed ticks!"
                )
            )

        config.getDoubleOrNull("damage")
            ?: violations.add(
                ConfigViolation(
                    "damage",
                    "You must specify the amount of damage to deal!"
                )
            )

        config.getIntOrNull("interval")
            ?: violations.add(
                ConfigViolation(
                    "interval",
                    "You must specify the tick delay between damages!"
                )
            )

        return violations
    }
}