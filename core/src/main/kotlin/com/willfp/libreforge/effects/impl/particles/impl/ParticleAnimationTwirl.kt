package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.lerp
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import kotlin.math.PI

object ParticleAnimationTwirl : ParticleAnimation<NoCompileData>("twirl") {
    override val arguments = arguments {
        require("small-radius", "You must specify the small radius!")
        require("large-radius", "You must specify the large radius!")
        require("duration", "You must specify the duration!")
        require("start-height", "You must specify the start height!")
        require("end-height", "You must specify the end height!")
        require("speed", "You must specify the speed!")
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
        val small = config.getDoubleFromExpression("small-radius", player)
        val large = config.getDoubleFromExpression("large-radius", player)
        val duration = config.getDoubleFromExpression("duration", player)
        val radius = lerp(small, large, tick / duration)
        val startHeight = config.getDoubleFromExpression("start-height", player)
        val endHeight = config.getDoubleFromExpression("end-height", player)
        val height = lerp(startHeight, endHeight, tick / duration)
        val speed = config.getDoubleFromExpression("speed", player)

        return setOf(
            Float3(
                (NumberUtils.fastSin(2 * PI * tick / duration * speed) * radius).toFloat(),
                height.toFloat(),
                (NumberUtils.fastCos(2 * PI * tick / duration * speed) * radius).toFloat()
            ).let {
                if (tick % 2 == 0) {
                    it.x = -it.x
                    it.z = -it.z
                    it
                } else it
            } + location
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
        return tick >= config.getIntFromExpression("duration", player)
    }
}
