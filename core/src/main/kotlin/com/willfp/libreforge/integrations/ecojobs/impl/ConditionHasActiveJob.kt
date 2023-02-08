package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionHasActiveJob : Condition<NoCompileData>("has_active_job") {
    override val arguments = arguments {
        require("job", "You must specify the job!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return EcoJobsAPI.instance.getActiveJob(player)?.id ==
                config.getString("job").lowercase()
    }
}
