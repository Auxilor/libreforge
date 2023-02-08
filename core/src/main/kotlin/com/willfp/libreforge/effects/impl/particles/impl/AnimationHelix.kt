package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import kotlin.math.PI

object AnimationHelix : ParticleAnimation<NoCompileData>("helix") {
    override val arguments = arguments {
        require("height", "You must specify the height!")
        require("duration", "You must specify the duration!")
        require("speed", "You must specify the speed!")
        require("radius", "You must specify the radius!")
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

        val x = NumberUtils.fastCos(tick / (2 * PI) * speed) * radius
        val y = height * (tick % duration) / duration
        val z = NumberUtils.fastSin(tick / (2 * PI) * speed) * radius

        return setOf(location + Float3(x.toFloat(), y.toFloat(), z.toFloat()))
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
