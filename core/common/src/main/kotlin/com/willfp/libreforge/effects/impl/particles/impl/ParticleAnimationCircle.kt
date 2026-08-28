package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.effects.impl.particles.ParticleAnimationCircleSpec
import com.willfp.libreforge.plusAssign
import com.willfp.libreforge.rotate
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import kotlin.math.PI

object ParticleAnimationCircle : ParticleAnimation<NoCompileData>("circle") {
    override val schema = ParticleAnimationCircleSpec::class

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius of the circle in blocks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
        require(
            "duration",
            "You must specify the duration!",
            description = "How long in ticks the animation runs for, and the period of one full circle. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "40"
        )
        require(
            "height",
            "You must specify the height!",
            description = "The height above the anchor location to draw the circle at. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1"
        )
        require(
            "pitch",
            "You must specify the pitch!",
            description = "The pitch to rotate the circle by, in degrees. 0 draws a flat, horizontal circle. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0"
        )
        require(
            "roll",
            "You must specify the roll!",
            description = "The roll to rotate the circle by, in degrees. 0 draws a flat, horizontal circle. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0"
        )
    }

    override fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        config: Config,
        player: Player,
        compileData: NoCompileData
    ): Collection<Float3> {
        val radius = config.getDoubleFromExpression("radius", player)
        val duration = config.getIntFromExpression("duration", player)
        val height = config.getDoubleFromExpression("height", player)

        val pitch = Math.toRadians(config.getDoubleFromExpression("pitch", player))
        val roll = Math.toRadians(config.getDoubleFromExpression("roll", player))

        val circleVector = Float3(
            (NumberUtils.fastSin(2 * PI * tick / duration) * radius).toFloat(),
            0f,
            (NumberUtils.fastCos(2 * PI * tick / duration) * radius).toFloat()
        )

        circleVector.rotate(
            0f, pitch.toFloat(), roll.toFloat()
        )

        // Add height after to not break circle's center
        circleVector += Float3(0f, height.toFloat(), 0f)

        return setOf(location + circleVector)
    }

    override fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        config: Config,
        player: Player,
        compileData: NoCompileData
    ): Boolean {
        return tick >= config.getIntFromExpression("duration", player)
    }
}
