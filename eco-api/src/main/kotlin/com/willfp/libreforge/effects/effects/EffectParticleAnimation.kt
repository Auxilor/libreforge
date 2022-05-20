package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.effects.particles.DirectionVector
import com.willfp.libreforge.effects.effects.particles.ParticleAnimations
import com.willfp.libreforge.effects.effects.particles.copy
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Location
import org.bukkit.Particle
import org.joml.Vector3f

class EffectParticleAnimation : Effect(
    "particle_animation",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val player = data.player ?: return
        val world = location.world ?: return

        val animation = ParticleAnimations.getByID(config.getString("animation")) ?: return
        val particle = Particle.valueOf(config.getString("particle").uppercase())

        var tick = 0

        plugin.runnableFactory.create {
            val playerVector = Vector3f(
                player.location.x.toFloat(),
                if (animation.useEyeLocation) player.eyeLocation.y.toFloat() else player.location.y.toFloat(),
                player.location.z.toFloat()
            )

            val playerDirectionVector = DirectionVector(
                player.location.yaw,
                player.location.pitch
            )

            val locationVector = Vector3f(
                location.x.toFloat(),
                location.y.toFloat(),
                location.z.toFloat()
            )

            val vectors = animation.getParticleLocations(
                tick,
                playerVector.copy(),
                playerDirectionVector.copy(),
                locationVector.copy()
            )

            for (vector in vectors) {
                world.spawnParticle(
                    particle,
                    Location(
                        world,
                        vector.x.toDouble(),
                        vector.y.toDouble(),
                        vector.z.toDouble()
                    ),
                    animation.particleAmount,
                    0.0, 0.0, 0.0, 0.0, null
                )
            }

            if (vectors.any { v ->
                    animation.shouldStopTicking(
                        tick,
                        playerVector.copy(),
                        playerDirectionVector.copy(),
                        locationVector.copy(),
                        v
                    )
                }) {
                it.cancel()
            }

            tick++
        }.runTaskTimerAsynchronously(1, 1)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("particle")) violations.add(
            ConfigViolation(
                "particle",
                "You must specify the particle!"
            )
        )

        if (!config.has("animation")) violations.add(
            ConfigViolation(
                "animation",
                "You must specify the animation!"
            )
        )

        return violations
    }
}
