package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object AnimationGroundSpiral : ParticleAnimation<NoCompileData>("ground_spiral") {
    override val arguments = arguments {
        require("scalar", "You must specify the scalar!")
        require("distance-scalar", "You must specify the distance scalar!")
        require("duration", "You must specify the duration!")
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
        val dx = cos(tick.toDouble() * 2 * PI / 20 * config.getDoubleFromExpression("scalar", player))
        val dz = sin(tick.toDouble() * 2 * PI / 20 * config.getDoubleFromExpression("scalar", player))

        return listOf(-1, 1).map {
            entityLocation + Float3(
                dx.toFloat() * it * 3 * tick / 20 * config.getDoubleFromExpression("distance-scalar", player).toFloat(),
                0.0f,
                dz.toFloat() * it * 3 * tick / 20 * config.getDoubleFromExpression("distance-scalar", player).toFloat()
            )
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
        return tick > config.getIntFromExpression("duration", player)
    }
}
