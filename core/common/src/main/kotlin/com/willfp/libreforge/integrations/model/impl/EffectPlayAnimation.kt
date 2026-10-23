package com.willfp.libreforge.integrations.model.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelManager
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
    override val description = "Plays a ModelEngine or BetterModel animation on the triggering entity, or on the victim if the triggering entity has no model."
    override val categories = setOf("visual", "entity")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "animation",
            "You must specify the animation name!",
            description = "The name of the animation to play.",
            type = ArgType.STRING
        )
        optional(
            "loop",
            description = "If the animation should loop until it is stopped, whatever loop mode the model gives it.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "mode",
            description = "How the animation repeats: PLAY_ONCE, LOOP or HOLD_ON_LAST. Leave out to use the model's own loop mode, or loop if loop is true.",
            type = ArgType.STRING,
            choices = listOf("PLAY_ONCE", "LOOP", "HOLD_ON_LAST")
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
            "start",
            description = "The same as lerp_in, kept for older BetterModel configs.",
            type = ArgType.INT
        )
        optional(
            "end",
            description = "The same as lerp_out, kept for older BetterModel configs.",
            type = ArgType.INT
        )
        optional(
            "force",
            description = "If the animation should restart when it is already playing.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "override",
            description = "The same as force, kept for older BetterModel configs.",
            type = ArgType.BOOLEAN
        )
        optional(
            "self",
            description = "If the animation should only be shown to the triggering player. BetterModel only.",
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

        val played = ModelManager.playAnimation(
            entity,
            modelId,
            animation,
            ModelAnimation.builder()
                .loopMode(loopMode(config, data))
                .speed(if (config.has("speed")) config.getDoubleFromExpression("speed", data) else 1.0)
                .lerpIn(ticks(config, data, "lerp_in", "start") / TICKS_PER_SECOND)
                .lerpOut(ticks(config, data, "lerp_out", "end") / TICKS_PER_SECOND)
                .force(config.getBool("force") || config.getBool("override"))
                .viewer(if (config.getBool("self")) data.player else null)
                .build()
        )

        val duration = config.getIntFromExpression("duration", data)
        if (played && duration > 0) {
            plugin.scheduler.on(entity).runLater(duration.toLong()) {
                ModelManager.stopAnimation(entity, modelId, animation, true)
            }
        }

        return played
    }

    private fun loopMode(config: Config, data: TriggerData): ModelAnimation.LoopMode =
        when (config.getFormattedString("mode", data).uppercase()) {
            "LOOP" -> ModelAnimation.LoopMode.LOOP
            "HOLD_ON_LAST" -> ModelAnimation.LoopMode.HOLD
            "PLAY_ONCE" -> ModelAnimation.LoopMode.ONCE
            else -> if (config.getBool("loop")) ModelAnimation.LoopMode.LOOP else ModelAnimation.LoopMode.MODEL
        }

    private fun ticks(config: Config, data: TriggerData, key: String, legacyKey: String): Double =
        if (config.has(key)) config.getDoubleFromExpression(key, data) else config.getDouble(legacyKey)

    private const val TICKS_PER_SECOND = 20.0
}
