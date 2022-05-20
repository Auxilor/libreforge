package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.entity.Player
import org.joml.Vector3f

object AnimationTrace : ParticleAnimation(
    "trace"
) {
    override val useEyeLocation = true

    override fun getParticleLocations(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        return setOf(
            location.copy().add(
                entityLocation.copy().sub(location).normalize()
                    .mul(0.6f * config.getDoubleFromExpression("spacing", player).toFloat())
            )
        )
    }

    override fun shouldStopTicking(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f,
        config: Config,
        player: Player
    ): Boolean {
        return entityLocation.distance(lastLocation) < 1 || tick > 100
    }
}
