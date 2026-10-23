package com.willfp.libreforge.integrations.modelengine.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData

object EffectStopAnimation : Effect<NoCompileData>("stop_animation") {
    override val description = "Stops a ModelEngine animation on the triggering entity, or on the victim if the triggering entity has no model."
    override val categories = setOf("visual", "entity")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "animation",
            "You must specify the animation name!",
            description = "The name of the ModelEngine animation to stop.",
            type = ArgType.STRING
        )
        optional(
            "fade",
            description = "If the animation should blend out over its lerp out time. If false, it stops straight away.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "model",
            description = "Only stop the animation on the model with this ID. Leave out to stop it on every model on the entity.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.modelTarget() ?: return false
        val animation = config.getFormattedString("animation", data)

        val handlers = animationHandlers(entity, config.getStringOrNull("model"))
            .filter { it.isPlayingAnimation(animation) }

        for (handler in handlers) {
            if (config.getBoolOrNull("fade") ?: true) {
                handler.stopAnimation(animation)
            } else {
                handler.forceStopAnimation(animation)
            }
        }

        return handlers.isNotEmpty()
    }
}
