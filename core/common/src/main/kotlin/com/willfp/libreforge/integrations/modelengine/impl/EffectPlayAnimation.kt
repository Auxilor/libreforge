package com.willfp.libreforge.integrations.modelengine.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData

object EffectPlayAnimation : Effect<NoCompileData>("play_animation") {
    override val description = "Plays a ModelEngine animation on the triggering entity, or on the victim if the triggering entity has no model."
    override val categories = setOf("visual", "entity")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "animation",
            "You must specify the animation name!",
            description = "The name of the ModelEngine animation to play.",
            type = ArgType.STRING
        )
        optional(
            "loop",
            description = "If the animation should loop until it is stopped, whatever loop mode the model gives it.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "duration",
            description = "Ticks to play the animation for before stopping it. Leave out to let it finish on its own, or loop forever when looping.",
            type = ArgType.EXPRESSION
        )
        optional(
            "speed",
            description = "The playback speed, where 1 is normal speed.",
            type = ArgType.EXPRESSION,
            default = "1"
        )
        optional(
            "lerp_in",
            description = "Ticks to blend into the animation.",
            type = ArgType.EXPRESSION,
            default = "0"
        )
        optional(
            "lerp_out",
            description = "Ticks to blend out of the animation when it stops.",
            type = ArgType.EXPRESSION,
            default = "0"
        )
        optional(
            "force",
            description = "If the animation should restart when it is already playing.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "model",
            description = "Only play the animation on the model with this ID. Leave out to play it on every model on the entity.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.modelTarget() ?: return false
        val animation = config.getFormattedString("animation", data)
        val modelId = config.getStringOrNull("model")

        val handlers = animationHandlers(entity, modelId).filter {
            it.playAnimation(
                animation,
                config.getDoubleFromExpression("lerp_in", data) / TICKS_PER_SECOND,
                config.getDoubleFromExpression("lerp_out", data) / TICKS_PER_SECOND,
                if (config.has("speed")) config.getDoubleFromExpression("speed", data) else 1.0,
                config.getBool("force"),
                config.getBool("loop")
            )
        }

        val duration = config.getIntFromExpression("duration", data)
        if (duration > 0 && handlers.isNotEmpty()) {
            plugin.scheduler.on(entity).runLater(duration.toLong()) {
                animationHandlers(entity, modelId).forEach { it.stopAnimation(animation) }
            }
        }

        return handlers.isNotEmpty()
    }

    private const val TICKS_PER_SECOND = 20.0
}
