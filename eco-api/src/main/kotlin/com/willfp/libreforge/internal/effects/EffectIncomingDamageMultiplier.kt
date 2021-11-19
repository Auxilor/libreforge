package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.effects.Effect
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class EffectIncomingDamageMultiplier : Effect("incoming_damage_multiplier") {
    override fun onIncomingDamage(
        victim: Player,
        event: EntityDamageEvent,
        config: JSONConfig
    ) {
        event.damage *= config.getDouble("multiplier")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the incoming damage multiplier!"
                )
            )

        return violations
    }
}