package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.ecojobs.api.event.PlayerJobLevelUpEvent
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasJobLevel : Condition<NoCompileData>("has_job_level") {
    override val arguments = arguments {
        require("job", "You must specify the job!")
        require("level", "You must specify the level!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return EcoJobsAPI.instance.getJobLevel(
            player,
            Jobs.getByID(config.getString("job").lowercase()) ?: return false
        ) >= config.getIntFromExpression("level", player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerJobLevelUpEvent) {
        event.player.updateEffects()
    }
}
