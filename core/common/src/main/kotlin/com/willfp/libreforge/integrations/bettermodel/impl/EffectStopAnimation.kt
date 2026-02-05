package com.willfp.libreforge.integrations.bettermodel.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.get
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import org.bukkit.entity.LivingEntity
import kotlin.jvm.optionals.getOrNull

object EffectStopAnimation : Effect<NoCompileData>("stop_animation") {
    override val isPermanent = false

    override val arguments = arguments {
        require("model", "You must specify the animation model!")
        require("animation", "You must specify the animation name!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.dispatcher.get<LivingEntity>() ?: data.victim ?: return false

        val animationModel = config.getFormattedString("model", data)
        val animationName = config.getFormattedString("animation", data)

        val modeledEntity = BukkitAdapter.adapt(entity)

        val tracker = modeledEntity.tracker(animationModel).getOrNull() ?: return false

        tracker.stopAnimation(animationName)

        return true
    }
}
