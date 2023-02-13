package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.impl.particles.impl.AnimationCircle
import com.willfp.libreforge.effects.impl.particles.impl.AnimationDoubleHelix
import com.willfp.libreforge.effects.impl.particles.impl.AnimationGroundSpiral
import com.willfp.libreforge.effects.impl.particles.impl.AnimationHelix
import com.willfp.libreforge.effects.impl.particles.impl.AnimationTrace
import com.willfp.libreforge.effects.impl.particles.impl.AnimationTwirl

@Suppress("UNUSED")
object ParticleAnimations {
    private val registry = mutableMapOf<String, ParticleAnimation<*>>()

    /**
     * Get an animation by [id].
     */
    fun getByID(id: String): ParticleAnimation<*>? {
        return registry[id]
    }

    /**
     * Register a new [animation].
     */
    fun register(animation: ParticleAnimation<*>) {
        registry[animation.id] = animation
    }

    /**
     * Compile a [config] into a ParticleAnimationBloc in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): ParticleAnimationBlock<*>? {
        val animation = getByID(config.getString("id"))

        if (animation == null) {
            context.log(ConfigViolation("id", "Invalid shape ID specified!"))
            return null
        }

        return makeBlock(animation, config.getSubsection("particle-args"), context.with("shape args"))
    }

    private fun <T> makeBlock(
        animation: ParticleAnimation<T>,
        config: Config,
        context: ViolationContext
    ): ParticleAnimationBlock<T>? {
        if (!animation.checkConfig(config, context)) {
            return null
        }

        val compileData = animation.makeCompileData(config, context)

        return ParticleAnimationBlock(
            animation,
            config,
            compileData,
        )
    }

    init {
        register(AnimationCircle)
        register(AnimationDoubleHelix)
        register(AnimationGroundSpiral)
        register(AnimationHelix)
        register(AnimationTrace)
        register(AnimationTwirl)
    }
}