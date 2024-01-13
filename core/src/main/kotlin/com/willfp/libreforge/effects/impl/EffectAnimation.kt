package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.animations.AnimationBlock
import com.willfp.libreforge.effects.impl.animations.Animations
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.xz

object EffectAnimation : Effect<AnimationBlock<*, *>?>("animation") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("animation", "You must specify a valid animation!", Config::getString) {
            Animations[it] != null
        }

        inherit("animation_args") { Animations[it.getString("animation")] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: AnimationBlock<*, *>?): Boolean {
        val location = data.location?.clone() ?: return false
        compileData ?: return false

        var tick = 0

        fun <T, T2> playAnimation(animationBlock: AnimationBlock<T, T2>) {
            val animationData = animationBlock.setUp(
                location,
                location.direction.toFloat3(),
                data
            )

            plugin.runnableFactory.create {
                if (
                    animationBlock.play(
                        tick,
                        location,
                        location.direction.toFloat3(),
                        data,
                        animationData
                    )
                ) {
                    animationBlock.finish(
                        location,
                        location.direction.toFloat3(),
                        data,
                        animationData
                    )
                    it.cancel()
                }

                tick++
            }.runTaskTimer(0, 1)
        }

        playAnimation(compileData)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): AnimationBlock<*, *>? {
        return Animations.compile(
            config,
            context
        )
    }
}
