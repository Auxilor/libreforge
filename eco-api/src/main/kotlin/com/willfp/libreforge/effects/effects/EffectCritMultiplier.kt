package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EffectCritMultiplier : Effect(
    "crit_multiplier",
    supportsFilters = true,
    applicableTriggers = listOf(
        Triggers.TRIDENT_HIT,
        Triggers.ARROW_HIT,
        Triggers.MELEE_ATTACK
    )
) {
    override fun onAnyDamage(
        attacker: Player,
        victim: LivingEntity,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        if (attacker.velocity.y >= 0) {
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
                    "You must specify the crit damage multiplier!"
                )
            )

        return violations
    }
}