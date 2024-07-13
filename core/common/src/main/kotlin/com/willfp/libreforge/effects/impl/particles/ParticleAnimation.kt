package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Player
import java.util.Objects

abstract class ParticleAnimation<T>(
    override val id: String
) : Compilable<T>() {
    abstract fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        config: Config,
        player: Player,
        compileData: T
    ): Collection<Float3>

    abstract fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        config: Config,
        player: Player,
        compileData: T
    ): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is ParticleAnimation<*>) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
