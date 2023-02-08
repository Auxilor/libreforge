package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.distance
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.normalize
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player

object AnimationTrace : ParticleAnimation<NoCompileData>("trace") {
    override val arguments = arguments {
        require("spacing", "You must specify the spacing!")
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
