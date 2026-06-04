package com.willfp.libreforge.effects.impl.particles.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.particles.ParticleAnimation
import com.willfp.libreforge.rotateAroundY
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object ParticleAnimationProjectileTrail : ParticleAnimation<NoCompileData>("projectile_trail") {
    override val arguments = arguments {
        require("gap", "You must specify the gap between particles!")
    }

    override fun getParticleLocations(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float3,
        location: Float3,
        config: Config,
        player: Player,
        compileData: NoCompileData
    ): Collection<Float3> {
        if (tick == 0) {
            return emptySet()
        }

        val gap = config.getIntFromExpression("gap", player)

        if ((tick % gap) != 0) {
            return emptySet()
        }

        return setOf(entityLocation)
    }

    override fun shouldStopTicking(
        tick: Int,
        entityLocation: Float3,
        entityDirection: Float2,
        location: Float3,
        lastLocation: Float3,
        config: Config,
        player: Player,
        entity: Entity,
        compileData: NoCompileData
    ): Boolean {
        return entity.isOnGround || entity.isDead
    }
}
