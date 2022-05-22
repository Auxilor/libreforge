package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import org.bukkit.entity.Player
import org.joml.Vector3f
import kotlin.math.PI

object AnimationHelix : ParticleAnimation(
    "helix"
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

        val double = config.getBool("double")

        val sign = if (double) {
            if (tick % 2 == 0) -1 else 1
        } else {
            1
        }

        val x = NumberUtils.fastCos(tick / (2 * PI) * speed) * sign * radius
        val y = height * (tick % duration) / duration
        val z = NumberUtils.fastSin(tick / (2 * PI) * speed) * sign * radius

        return setOf(location.copy().add(x.toFloat(), y.toFloat(), z.toFloat()))
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
