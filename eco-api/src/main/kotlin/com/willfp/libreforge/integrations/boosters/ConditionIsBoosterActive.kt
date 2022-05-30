package com.willfp.libreforge.integrations.boosters

import com.willfp.boosters.boosters.Boosters
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsBoosterActive : Condition("is_booster_active") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return Boosters.getByID(config.getString("booster"))?.active != null
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("booster")) violations.add(
            ConfigViolation(
                "booster",
                "You must specify the booster!"
            )
        )

        return violations
    }
}
