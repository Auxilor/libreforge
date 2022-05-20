package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import org.bukkit.entity.Player
import org.joml.Vector3f
import java.util.*

abstract class ParticleAnimation(
    id: String
): ConfigurableProperty(id) {
    init {
        register()
    }

    private fun register() {
        ParticleAnimations.addNewCondition(this)
    }

    open val useEyeLocation: Boolean = false

    abstract fun getParticleLocations(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        config: Config,
        player: Player
    ): Iterable<Vector3f>

    abstract fun shouldStopTicking(
        tick: Int,
        entityLocation: Vector3f,
        entityDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f,
        config: Config,
        player: Player
    ): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is ParticleAnimation) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}