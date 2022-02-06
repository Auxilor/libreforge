package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers


class EffectDamageVictim : Effect(
    "damage_victim",
    supportsFilters = false,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val player = data.player

        val damage = config.getDoubleFromExpression("damage", player)

        if (config.getBool("use_source")) {
            victim.damage(damage, player)
        } else {
            victim.damage(damage)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("damage")) violations.add(
            ConfigViolation(
                "damage",
                "You must specify the damage to deal!"
            )
        )

        if (!config.has("use_source")) violations.add(
            ConfigViolation(
                "use_source",
                "You must specify if the player should be marked as the damage source!"
            )
        )

        return violations
    }
}
