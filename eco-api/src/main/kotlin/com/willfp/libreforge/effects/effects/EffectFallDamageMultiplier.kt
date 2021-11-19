package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.effects.Effect
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class EffectFallDamageMultiplier : Effect("fall_damage_multiplier") {
    override fun onIncomingDamage(
        victim: Player,
        event: EntityDamageEvent,
        config: JSONConfig
    ) {
        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }
        event.damage *= config.getDouble("multiplier")
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "multiplier",
                    "You must specify the incoming fall damage multiplier!"
                )
            )

        return violations
    }
}