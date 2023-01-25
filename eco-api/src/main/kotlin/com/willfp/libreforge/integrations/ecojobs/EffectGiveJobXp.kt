package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.giveJobExperience
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveJobXp : Effect(
    "give_job_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("job", "You must specify the job to give xp for!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.giveJobExperience(
            Jobs.getByID(config.getString("job")) ?: return,
            config.getDoubleFromExpression("amount", player)
        )
    }
}
