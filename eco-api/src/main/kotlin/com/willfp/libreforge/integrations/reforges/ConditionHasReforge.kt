package com.willfp.libreforge.integrations.reforges

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.reforges.util.ReforgeLookup
import org.bukkit.entity.Player

class ConditionHasReforge : Condition("has_reforge") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return ReforgeLookup.provideReforges(player).map { it.id }.containsIgnoreCase(config.getString("reforge"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("reforge")) violations.add(
            ConfigViolation(
                "reforge",
                "You must specify the reforge!"
            )
        )

        return violations
    }
}
