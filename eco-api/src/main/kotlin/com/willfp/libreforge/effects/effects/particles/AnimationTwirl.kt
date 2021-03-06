package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import org.bukkit.entity.Player
import org.joml.Vector3f
import kotlin.math.PI

object AnimationTwirl : ParticleAnimation(
    "twirl"
) {
    override fun getParticleLocations(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f> {
        val small = config.getDoubleFromExpression("small-radius", player)
        val large = config.getDoubleFromExpression("large-radius", player)
        val duration = config.getDoubleFromExpression("duration", player)
        val radius = lerp(small, large, tick / duration)
        val startHeight = config.getDoubleFromExpression("start-height", player)
        val endHeight = config.getDoubleFromExpression("end-height", player)
        val height = lerp(startHeight, endHeight, tick / duration)
        val speed = config.getDoubleFromExpression("speed", player)

        return setOf(
            Vector3f(
                (NumberUtils.fastSin(2 * PI * tick / duration * speed) * radius).toFloat(),
                height.toFloat(),
                (NumberUtils.fastCos(2 * PI * tick / duration * speed) * radius).toFloat()
            ).let {
                if (tick % 2 == 0) {
                    it.x = -it.x
                    it.z = -it.z
                    it
                } else it
            }.add(location)
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
        val duration = config.getDoubleFromExpression("duration", player)
        return tick >= duration
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("small-radius")) violations.add(
            ConfigViolation(
                "small-radius",
                "You must specify the small radius!"
            )
        )

        if (!config.has("large-radius")) violations.add(
            ConfigViolation(
                "large-radius",
                "You must specify the large radius!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration!"
            )
        )

        if (!config.has("start-height")) violations.add(
            ConfigViolation(
                "start-height",
                "You must specify the start height!"
            )
        )

        if (!config.has("start-height")) violations.add(
            ConfigViolation(
                "start-height",
                "You must specify the start height!"
            )
        )

        if (!config.has("end-height")) violations.add(
            ConfigViolation(
                "end-height",
                "You must specify the end height!"
            )
        )

        if (!config.has("speed")) violations.add(
            ConfigViolation(
                "speed",
                "You must specify the speed!"
            )
        )

        return violations
    }
}
