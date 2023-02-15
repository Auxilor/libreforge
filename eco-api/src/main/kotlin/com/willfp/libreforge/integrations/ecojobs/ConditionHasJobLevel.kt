package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.event.PlayerJobLevelUpEvent
import com.willfp.ecojobs.api.getJobLevel
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ConditionHasJobLevel : Condition("has_job_level") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerJobLevelUpEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.getJobLevel(
            Jobs.getByID(config.getString("job").lowercase()) ?: return false
        ) >= config.getIntFromExpression("level", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("job")) violations.add(
            ConfigViolation(
                "job",
                "You must specify the job!"
            )
        )

        if (!config.has("level")) violations.add(
            ConfigViolation(
                "level",
                "You must specify the job level!"
            )
        )

        return violations
    }
}
