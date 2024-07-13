package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.particles.ParticleAnimationBlock
import com.willfp.libreforge.effects.impl.particles.ParticleAnimations
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.toLocation
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.xz
import org.bukkit.entity.LivingEntity

object EffectParticleAnimation : Effect<ParticleAnimationBlock<*>?>("particle_animation") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("particle", "You must specify the particle!")
        require("animation", "You must specify a valid animation!", Config::getString) {
            ParticleAnimations[it] != null
        }

        inherit("particle_args") { ParticleAnimations[it.getString("animation")] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: ParticleAnimationBlock<*>?): Boolean {
        val location = data.location ?: return false
        val player = data.player ?: return false
        compileData ?: return false

        val entity = when (config.getString("entity").lowercase()) {
            "victim" -> data.victim
            "projectile" -> data.projectile
            else -> data.player
        } ?: return false

        val world = location.world ?: return false

        val particle = Particles.lookup(config.getString("particle"))

        var tick = 0

        val args = config.getSubsection("particle_args")

        plugin.runnableFactory.create {
            val entityVector = if (config.getBool("use-eye-location") && entity is LivingEntity) {
                entity.eyeLocation.toFloat3()
            } else {
                entity.location.toFloat3()
            }

            val entityDirectionVector = entity.location.toFloat3().xz

            val locationVector = location.toFloat3()

            val vectors = if (args.has("tick-multiplier")) {
                val mult = args.getIntFromExpression("tick-multiplier", data)

                val mockTicks = (tick * mult until (tick * mult) + mult)

                mockTicks.map { t ->
                    compileData.getParticleLocations(
                        t,
                        entityVector.copy(),
                        entityDirectionVector.copy(),
                        locationVector.copy(),
                        player
                    )
                }.flatten()
            } else {
                compileData.getParticleLocations(
                    tick,
                    entityVector.copy(),
                    entityDirectionVector.copy(),
                    locationVector.copy(),
                    player
                )
            }

            for (vector in vectors) {
                particle.spawn(
                    vector.toLocation(world),
                    config.getIntFromExpression("particle-amount", data)
                )
            }

            if (vectors.any { v ->
                    compileData.shouldStopTicking(
                        tick,
                        entityVector.copy(),
                        entityDirectionVector.copy(),
                        locationVector.copy(),
                        v,
                        player
                    )
                }) {
                it.cancel()
            }

            tick++
        }.runTaskTimerAsynchronously(1, 1)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ParticleAnimationBlock<*>? {
        return ParticleAnimations.compile(
            config,
            context
        )
    }
}
