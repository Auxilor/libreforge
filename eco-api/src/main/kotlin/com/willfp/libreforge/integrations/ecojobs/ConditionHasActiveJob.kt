package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionHasActiveJob : Condition("has_active_job") {
    override val arguments = arguments {
        require("job", "You must specify the job!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EcoJobsAPI.instance.getActiveJob(player)?.id ==
                config.getString("job").lowercase()
    }
}
