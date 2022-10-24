package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSpawnParticle : Effect(
    "spawn_particle",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val particle = Particles.lookup(config.getString("particle"))
        val amount = config.getIntFromExpression("amount", data)
        particle.spawn(location, amount)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("particle")) violations.add(
            ConfigViolation(
                "particle",
                "You must specify the particle!"
            )
        )

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of particles to spawn!"
            )
        )

        return violations
    }
}