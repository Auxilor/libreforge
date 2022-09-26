package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.ConfigViolation
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
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        EcoJobsAPI.instance.giveJobExperience(
            player,
            Jobs.getByID(config.getString("job")) ?: return,
            config.getDoubleFromExpression("amount", player)
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of xp to give!"
            )
        )

        if (!config.has("job")) violations.add(
            ConfigViolation(
                "job",
                "You must specify the job to give xp for!"
            )
        )

        return violations
    }
}
