package com.willfp.libreforge.effects.impl.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationCircle
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationDoubleHelix
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationGroundSpiral
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationHelix
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationTrace
import com.willfp.libreforge.effects.impl.particles.impl.ParticleAnimationTwirl

@Suppress("UNUSED")
object ParticleAnimations : Registry<ParticleAnimation<*>>() {
    /**
     * Compile a [config] into a ParticleAnimationBloc in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): ParticleAnimationBlock<*>? {
        val animationID = config.getString("animation")
        val animation = get(animationID)

        if (animation == null) {
            context.log(ConfigViolation("animation", "Invalid animation specified: ${animationID}!"))
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
        register(ParticleAnimationCircle)
        register(ParticleAnimationDoubleHelix)
        register(ParticleAnimationGroundSpiral)
        register(ParticleAnimationHelix)
        register(ParticleAnimationTrace)
        register(ParticleAnimationTwirl)
    }
}