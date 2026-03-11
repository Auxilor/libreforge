package com.willfp.libreforge.integrations.bettermodel.impl

import com.nexomc.nexo.utils.applyIf
import com.willfp.eco.core.config.interfaces.Config
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
    override val isPermanent = false

    override val arguments = arguments {
        require("animation", "You must specify the animation name!")
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
