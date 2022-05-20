package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import org.bukkit.entity.Player
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object AnimationGroundSpiral : ParticleAnimation(
    "ground_spiral"
) {
    override val particleAmount = 1

    override fun getParticleLocations(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        val dx = cos(tick.toDouble() * 2 * PI / 20 * config.getDoubleFromExpression("scalar", player))
        val dz = sin(tick.toDouble() * 2 * PI / 20 * config.getDoubleFromExpression("scalar", player))

        return listOf(-1, 1).map {
            playerLocation.copy().add(
                Vector3f(
                    dx.toFloat() * it * 3 * tick / 20 * config.getDoubleFromExpression("distance-scalar", player).toFloat(),
                    0.0f,
                    dz.toFloat() * it * 3 * tick / 20 * config.getDoubleFromExpression("distance-scalar", player).toFloat()
                )
            )
        }
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
        return tick > config.getIntFromExpression("duration", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("scalar")) violations.add(
            ConfigViolation(
                "scalar",
                "You must specify the scalar!"
            )
        )

        if (!config.has("distance-scalar")) violations.add(
            ConfigViolation(
                "distance-scalar",
                "You must specify the distance scalar!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration!"
            )
        )

        return violations
    }
}
