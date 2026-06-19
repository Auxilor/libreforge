package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.Objects

abstract class ParticleAnimation<T>(
    override val id: String
) : Compilable<T>() {

    /**
     * Get the particle locations with a 2D direction.
     *
     * This method exists for backwards compatibility.
     */
    open fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        config: Config,
        player: Player,
        compileData: T
    ): Collection<Float3> = emptySet()

    /**
     * Get the particle locations with a 3D direction.
     */
    open fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float3,
        location: Float3,
        config: Config,
        player: Player,
        compileData: T
    ): Collection<Float3> = emptySet()

    open fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        config: Config,
        player: Player,
        compileData: T
    ): Boolean = false

    open fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        config: Config,
        player: Player,
        entity: Entity, // Entity used for the animation
        compileData: T
    ): Boolean = false

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
