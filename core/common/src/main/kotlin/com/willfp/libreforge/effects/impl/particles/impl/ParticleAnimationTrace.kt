package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.distance
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.effects.impl.particles.ParticleAnimationTraceSpec
import com.willfp.libreforge.normalize
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player

object ParticleAnimationTrace : ParticleAnimation<NoCompileData>("trace") {
    override val schema = ParticleAnimationTraceSpec::class

    override val arguments = arguments {
        require(
            "spacing",
            "You must specify the spacing!",
            description = "The distance between each particle along the traced line. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0.5"
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
        return setOf(
            location + (entityLocation - location.normalize())
                    * (tick * config.getDoubleFromExpression("spacing", player).toFloat())
        )
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
        return location.distance(lastLocation) < 1 || tick > 100
    }
}
