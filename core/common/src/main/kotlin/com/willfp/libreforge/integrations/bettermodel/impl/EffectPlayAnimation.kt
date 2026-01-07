package com.willfp.libreforge.integrations.bettermodel.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.get
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.animation.AnimationIterator
import kr.toxicity.model.api.animation.AnimationModifier
import org.bukkit.entity.LivingEntity
import kotlin.jvm.optionals.getOrNull

object EffectPlayAnimation : Effect<NoCompileData>("play_animation") {
    override val isPermanent = false

    override val arguments = arguments {
        require("tracker", "You must specify the animation tracker!")
        require("animation", "You must specify the animation name!")
        require("type", "You must specify the animation type!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.dispatcher.get<LivingEntity>() ?: data.victim ?: return false

        val animationModel = config.getFormattedString("tracker", data)
        val animationName = config.getFormattedString("animation", data)
        val animationType = config.getFormattedString("type", data)

        val modeledEntity = BetterModel.registry(entity).getOrNull() ?: return false

        val mode = AnimationIterator.Type.entries.firstOrNull { it.name == animationType.uppercase() } ?: return false

        val modifier = AnimationModifier.builder()
            .player(data.player)
            .type(mode)
            .build()

        val tracker = modeledEntity.tracker(animationModel) ?: return false

        tracker.animate(animationName, modifier)

        return true
    }
}
