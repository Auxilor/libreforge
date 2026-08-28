package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.Objects
import kotlin.reflect.KClass

abstract class ParticleAnimation<T>(
    override val id: String
) : Compilable<T>() {
    /**
     * A documentation-only DTO describing the keys of the particle_args subsection for this
     * animation, in the same way as ArgumentMeta.Regular.schema. Non-null properties are
     * required keys, nullable properties are optional keys; KDoc `@property` lines supply
     * per-key descriptions.
     *
     * particle_args is a single subsection whose keys depend on the chosen animation, so each
     * animation declares its own schema rather than the effect declaring one for all of them.
     * The wiki parser reads this class from source — it is never instantiated at runtime.
     */
    open val schema: KClass<*>? = null

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
