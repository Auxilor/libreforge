package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent

class EffectCritMultiplier : Effect(
    "crit_multiplier",
    supportsFilters = true,
    applicableTriggers = listOf(
        Triggers.TRIDENT_ATTACK,
        Triggers.BOW_ATTACK,
        Triggers.MELEE_ATTACK
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val event = data.event as? WrappedDamageEvent ?: return
        val player = data.player ?: return

        if (player.velocity.y >= 0) {
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
