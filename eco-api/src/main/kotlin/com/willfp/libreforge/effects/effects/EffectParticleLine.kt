package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Particle
import kotlin.math.floor

class EffectParticleLine : Effect(
    "particle_line",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val player = data.player ?: return

        val world = location.world ?: return

        val startPos = player.location.toVector()
        val endPos = location.toVector()

        val distance = endPos.distance(startPos)
        val spacing = config.getDoubleFromExpression("spacing", player)
        val particles = floor(distance / spacing).toInt()
        val addVector = endPos.clone().subtract(startPos).normalize().multiply(spacing)

        val particle = Particle.valueOf(config.getString("particle").uppercase())
        val amount = config.getIntFromExpression("amount", data.player)

        var currentVector = startPos

        repeat(particles) {
            world.spawnParticle(particle, currentVector.toLocation(world), amount, 0.0, 0.0, 0.0, 0.0, null)
            currentVector = currentVector.add(addVector)
        }
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

        if (!config.has("spacing")) violations.add(
            ConfigViolation(
                "spacing",
                "You must specify the spacing between particles!"
            )
        )

        return violations
    }
}