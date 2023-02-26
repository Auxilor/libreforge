package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.giveJobExperience
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveJobXp : Effect<NoCompileData>("give_job_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("job", "You must specify the job to give xp for!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        player.giveJobExperience(
            Jobs.getByID(config.getString("job")) ?: return false,
            config.getDoubleFromExpression("amount", player)
        )

        return true
    }
}
