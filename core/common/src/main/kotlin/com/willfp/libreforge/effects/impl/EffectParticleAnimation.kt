package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.ArgType
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
    override val description = "Plays a particle animation at the trigger location over time using a named animation pattern."
    override val categories = setOf("visual")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "particle",
            "You must specify the particle!",
            description = "The particle type to spawn during the animation.",
            type = ArgType.STRING
        )
        require("animation", "You must specify a valid animation!", Config::getString) {
            ParticleAnimations[it] != null
        }
        describe(
            "animation",
            description = "The animation pattern to use (e.g. circle, helix).",
            type = ArgType.STRING,
            choices = listOf("circle", "double_helix", "ground_spiral", "helix", "trace", "twirl")
        )

        inherit("particle_args") { ParticleAnimations[it.getString("animation")] }
        optional(
            "particle-amount",
            description = "The number of particles to spawn per animation point per tick. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1"
        )
        optional(
            "use-eye-location",
            description = "Whether to use the entity's eye location instead of their feet.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
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

            val entityDirectionVector = entity.location.direction.toFloat3().xz
            val entityDirectionFloat3Vector = entity.location.direction.toFloat3()

            val locationVector = location.toFloat3()

            val vectors = if (args.has("tick-multiplier")) {
                val mult = args.getIntFromExpression("tick-multiplier", data)

                val mockTicks = (tick * mult until (tick * mult) + mult)

                mockTicks.flatMap { t ->
                    compileData.getParticleLocations(
                        t,
                        entityVector.copy(),
                        entityDirectionVector.copy(),
                        locationVector.copy(),
                        player,
                        entityDirectionFloat3Vector.copy()
                    )
                }
            } else {
                compileData.getParticleLocations(
                    tick,
                    entityVector.copy(),
                    entityDirectionVector.copy(),
                    locationVector.copy(),
                    player,
                    entityDirectionFloat3Vector.copy()
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
                        player,
                        entity
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
