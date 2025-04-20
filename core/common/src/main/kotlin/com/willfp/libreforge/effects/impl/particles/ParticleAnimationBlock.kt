package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player

/**
 * A single animation config block.
 */
class ParticleAnimationBlock<T>(
    val animation: ParticleAnimation<T>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        player: Player
    ): Collection<Float3> =
        animation.getParticleLocations(
            tick,
            entityLocation,
            entityDirection,
            location,
            config,
            player,
            compileData
        )

    fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        player: Player
    ): Boolean = animation.shouldStopTicking(
        tick,
        entityLocation,
        entityDirection,
        location,
        lastLocation,
        config,
        player,
        compileData
    )
}
