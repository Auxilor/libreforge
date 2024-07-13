package com.willfp.libreforge.effects.impl.animations

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.impl.animations.impl.AnimationSpinItem


@Suppress("UNUSED")
object Animations : Registry<Animation<*, *>>() {
    /**
     * Compile a [config] into an AnimationBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): AnimationBlock<*, *>? {
        val animationID = config.getString("animation")
        val animation = get(animationID)

        if (animation == null) {
            context.log(ConfigViolation("animation", "Invalid animation specified: ${animationID}!"))
            return null
        }

        return makeBlock(animation, config.getSubsection("animation_args"), context.with("animation args"))
    }

    private fun <T, T2> makeBlock(
        animation: Animation<T, T2>,
        config: Config,
        context: ViolationContext
    ): AnimationBlock<T, T2>? {
        if (!animation.checkConfig(config, context)) {
            return null
        }

        val compileData = animation.makeCompileData(config, context)

        return AnimationBlock(
            animation,
            config,
            compileData,
        )
    }

    init {
        register(AnimationSpinItem)
    }
}
