package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.effects.impl.particles.ParticleAnimationDoubleHelixSpec
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import kotlin.math.PI

object ParticleAnimationDoubleHelix : ParticleAnimation<NoCompileData>("double_helix") {
    override val schema = ParticleAnimationDoubleHelixSpec::class

    override val arguments = arguments {
        require(
            "height",
            "You must specify the height!",
            description = "The total height the helix climbs over one duration. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "2"
        )
        require(
            "duration",
            "You must specify the duration!",
            description = "How long in ticks the animation runs for. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "40"
        )
        require(
            "speed",
            "You must specify the speed!",
            description = "How quickly the helix rotates as it climbs. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1"
        )
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius of each strand of the helix in blocks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1"
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
        val height = config.getDoubleFromExpression("height", player)
        val duration = config.getIntFromExpression("duration", player)
        val speed = config.getDoubleFromExpression("speed", player)
        val radius = config.getDoubleFromExpression("radius", player)

        val vector = Float3(
            (NumberUtils.fastCos(tick / (2 * PI) * speed) * radius).toFloat(),
            (height * (tick % duration) / duration).toFloat(),
            (NumberUtils.fastSin(tick / (2 * PI) * speed) * radius).toFloat(),
        )

        return arrayOf(-1f, 1f).map {
            location + vector * it
        }
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
