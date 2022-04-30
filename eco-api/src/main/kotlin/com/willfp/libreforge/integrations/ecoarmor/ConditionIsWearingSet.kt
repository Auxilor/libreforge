package com.willfp.libreforge.integrations.ecoarmor

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsWearingSet : Condition("is_wearing_set") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return ArmorUtils.getSetOnPlayer(player)?.id == config.getString("set")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("set")) violations.add(
            ConfigViolation(
                "set",
                "You must specify the set name!"
            )
        )

        return violations
    }
}
