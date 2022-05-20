package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.entity.Player
import org.joml.Vector3f

object AnimationTrace : ParticleAnimation(
    "trace"
) {
    override val particleAmount = 1
    override val useEyeLocation = true

    override fun getParticleLocations(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        return setOf(
            location.copy().add(
                playerLocation.copy().sub(location).normalize()
                    .mul(0.6f * config.getDoubleFromExpression("spacing", player).toFloat())
            )
        )
    }

    override fun shouldStopTicking(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f,
        config: Config,
        player: Player
    ): Boolean {
        return playerLocation.distance(lastLocation) < 1 || tick > 100
    }
}
