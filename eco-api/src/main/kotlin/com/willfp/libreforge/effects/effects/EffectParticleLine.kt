package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import kotlin.math.floor

class EffectParticleLine : Effect(
    "particle_line",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    override val arguments = arguments {
        require("particle", "You must specify the particle!")
        require("amount", "You must specify the amount of particles to spawn!")
        require("spacing", "You must specify the spacing between particles!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val player = data.player ?: return

        val world = location.world ?: return

        val startPos = player.location.toVector()
        val endPos = location.toVector()

        val distance = endPos.distance(startPos)
        val spacing = config.getDoubleFromExpression("spacing", data)
        val particles = floor(distance / spacing).toInt()
        val addVector = endPos.clone().subtract(startPos).normalize().multiply(spacing)

        val particle = Particles.lookup(config.getString("particle"))
        val amount = config.getIntFromExpression("amount", data)

        var currentVector = startPos

        repeat(particles) {
            particle.spawn(currentVector.toLocation(world), amount)
            currentVector = currentVector.add(addVector)
        }
    }
}
