package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import org.bukkit.entity.Player
import org.joml.Vector3f
import kotlin.math.PI

object AnimationDoubleHelix : ParticleAnimation(
    "double_helix"
) {
    override fun getParticleLocations(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        val height = config.getDoubleFromExpression("height", player)
        val duration = config.getIntFromExpression("duration", player)
        val speed = config.getDoubleFromExpression("speed", player)
        val radius = config.getDoubleFromExpression("radius", player)

        val vector = Vector3f(
            (NumberUtils.fastCos(tick / (2 * PI) * speed) * radius).toFloat(),
            (height * (tick % duration) / duration).toFloat(),
            (NumberUtils.fastSin(tick / (2 * PI) * speed) * radius).toFloat(),
        )

        return arrayOf(-1f, 1f).map {
            location.copy().add(vector.copy().mul(it))
        }
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
        val duration = config.getIntFromExpression("duration", player)
        return tick >= duration
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("height")) violations.add(
            ConfigViolation(
                "height",
                "You must specify the height!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration!"
            )
        )

        if (!config.has("speed")) violations.add(
            ConfigViolation(
                "speed",
                "You must specify the speed!"
            )
        )

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius!"
            )
        )

        return violations
    }
}
