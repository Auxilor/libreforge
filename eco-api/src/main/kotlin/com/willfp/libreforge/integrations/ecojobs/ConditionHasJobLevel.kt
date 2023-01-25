package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.event.PlayerJobLevelUpEvent
import com.willfp.ecojobs.api.getJobLevel
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ConditionHasJobLevel : Condition("has_job_level") {
    override val arguments = arguments {
        require("job", "You must specify the job!")
        require("level", "You must specify the level!")
    }

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
}
