package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player


class ConditionHasPermission : Condition("is_sneaking") {
    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return player.hasPermission(config.getString("permission"))
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getStringOrNull("permission", false)
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "permission",
                    "You must specify the permission!"
                )
            )

        return violations
    }
}