package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.arguments
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
    override val arguments = arguments {
        require("particle", "You must specify the particle!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val particle = Particles.lookup(config.getString("particle"))
        val amount = if (config.has("amount")) config.getIntFromExpression("amount", data) else 1
        particle.spawn(location, amount)
    }
}
