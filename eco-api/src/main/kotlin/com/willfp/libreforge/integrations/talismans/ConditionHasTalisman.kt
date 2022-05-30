package com.willfp.libreforge.integrations.talismans

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.talismans.talismans.util.TalismanChecks
import org.bukkit.entity.Player

class ConditionHasTalisman : Condition("has_talisman") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return TalismanChecks.getTalismansOnPlayer(player).map { it.id }.containsIgnoreCase(config.getString("talisman"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("talisman")) violations.add(
            ConfigViolation(
                "talisman",
                "You must specify the talisman!"
            )
        )

        return violations
    }
}
