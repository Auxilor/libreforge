package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.rotateAroundY
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player

object ParticleAnimationWingTips : ParticleAnimation<NoCompileData>("wing_tips") {
    override fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float3,
        location: Float3,
        config: Config,
        player: Player,
        compileData: NoCompileData
    ): Collection<Float3> {
        val point1 = entityLocation + (entityDirection * 1.2f).rotateAroundY(Math.toRadians(90.0).toFloat())
        val point2 = entityLocation + (entityDirection * 1.2f).rotateAroundY(Math.toRadians(-90.0).toFloat())

        return setOf(
            point1,
            point2
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
        return true
    }
}
