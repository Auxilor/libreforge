package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasPotionEffect : Condition("has_potion_effect") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.activePotionEffects.map { it.type.name }.containsIgnoreCase(
            config.getString("effect")
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("effect")) violations.add(
            ConfigViolation(
                "effect",
                "You must specify the potion effect!"
            )
        )

        return violations
    }
}