package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player


class ConditionHasPermission : Condition("has_permission") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.hasPermission(config.getString("permission"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("permission", false)
            ?: violations.add(
                ConfigViolation(
                    "permission",
                    "You must specify the permission!"
                )
            )

        return violations
    }
}