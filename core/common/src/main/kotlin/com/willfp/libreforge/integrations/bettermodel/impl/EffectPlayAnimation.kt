package com.willfp.libreforge.integrations.bettermodel.impl

import com.nexomc.nexo.utils.applyIf
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.get
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.animation.AnimationIterator
import kr.toxicity.model.api.animation.AnimationModifier
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import org.bukkit.entity.LivingEntity

object EffectPlayAnimation : Effect<NoCompileData>("play_animation") {
    override val description = "Plays a BetterModel animation on the triggering entity or victim."
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
            "mode",
            description = "The animation playback mode (e.g. PLAY_ONCE, LOOP). Defaults to PLAY_ONCE.",
            type = ArgType.STRING,
            default = "PLAY_ONCE"
        )
        optional(
            "self",
            description = "Whether to show the animation only to the triggering player.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "override",
            description = "Whether to override any currently playing animation.",
            type = ArgType.BOOLEAN
        )
        optional(
            "speed",
            description = "Playback speed multiplier for the animation.",
            type = ArgType.DOUBLE
        )
        optional(
            "start",
            description = "The frame index at which to start the animation.",
            type = ArgType.INT
        )
        optional(
            "end",
            description = "The frame index at which to end the animation.",
            type = ArgType.INT
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.dispatcher.get<LivingEntity>() ?: data.victim ?: return false
        val player = data.player

        val animation = config.getFormattedString("animation", data)
        val mode =
            enumValueOfOrNull<AnimationIterator.Type>(
                config.getFormattedString("mode", data).uppercase()
            ) ?: AnimationIterator.Type.PLAY_ONCE

        val registry = BetterModel.registryOrNull(entity.uniqueId) ?: return false

        val modifier = AnimationModifier.builder()
            .applyIf(config.getBool("self")) {
                player(player?.let { BukkitAdapter.adapt(player) })
            }
            .applyIf(config.has("override")) {
                override(config.getBool("override"))
            }
            .applyIf(config.has("speed")) {
                speed(config.getDouble("speed").toFloat())
            }
            .applyIf(config.has("start")) {
                start(config.getInt("start"))
            }
            .applyIf(config.has("end")) {
                end(config.getInt("end"))
            }
            .type(mode)
            .build()

        registry.trackers().forEach {
            it.animate(animation, modifier)
        }

        return true
    }
}
