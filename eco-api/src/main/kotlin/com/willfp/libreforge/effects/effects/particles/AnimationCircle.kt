package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import org.bukkit.entity.Player
import org.joml.Vector3f
import kotlin.math.PI

object AnimationCircle : ParticleAnimation(
    "circle"
) {
    override fun getParticleLocations(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        val radius = config.getDoubleFromExpression("radius", player)
        val duration = config.getIntFromExpression("duration", player)
        val height = config.getDoubleFromExpression("height", player)

        val pitch = Math.toRadians(config.getDoubleFromExpression("pitch", player))
        val roll = Math.toRadians(config.getDoubleFromExpression("roll", player))

        val circleVector = Vector3f(
            (NumberUtils.fastSin(2 * PI * tick / duration) * radius).toFloat(),
            0f,
            (NumberUtils.fastCos(2 * PI * tick / duration) * radius).toFloat()
        )

        circleVector.rotate(0f, pitch.toFloat(), roll.toFloat())
        // Add height after to not break circle's center
        circleVector.add(0f, height.toFloat(), 0f)

        return setOf(
            location.add(circleVector)
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
        val duration = config.getIntFromExpression("duration", player)
        return tick >= duration
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the circle radius!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration!"
            )
        )

        if (!config.has("height")) violations.add(
            ConfigViolation(
                "height",
                "You must specify the height!"
            )
        )

        if (!config.has("pitch")) violations.add(
            ConfigViolation(
                "pitch",
                "You must specify the pitch!"
            )
        )

        if (!config.has("roll")) violations.add(
            ConfigViolation(
                "roll",
                "You must specify the roll!"
            )
        )

        return violations
    }
}
