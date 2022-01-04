package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsGliding: Condition("is_gliding") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isGliding == config.getBool("is_gliding")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getBoolOrNull("is_gliding")
            ?: violations.add(
                ConfigViolation(
                    "is_gliding",
                    "You must specify if the player must be gliding or not!"
                )
            )

        return violations
    }
}