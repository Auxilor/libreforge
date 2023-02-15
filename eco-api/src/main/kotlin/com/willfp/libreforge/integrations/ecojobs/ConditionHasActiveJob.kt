package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.activeJobs
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasActiveJob : Condition("has_active_job") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.activeJobs.any { it.id == config.getString("job").lowercase() }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("job")) violations.add(
            ConfigViolation(
                "job", "You must specify the job!"
            )
        )

        return violations
    }
}
