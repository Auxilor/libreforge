package com.willfp.libreforge.integrations.modelengine.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.get
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.modelenginebridge.ModelEngineBridge
import org.bukkit.entity.LivingEntity

object EffectPlayAnimation : Effect<NoCompileData>("play_animation") {
    override val description = "Plays a ModelEngine animation on the triggering entity or victim."
    override val categories = setOf("visual", "entity")

    override val isPermanent = false

    override val arguments = arguments {
        require("animation", "You must specify the animation name!", description = "The name of the ModelEngine animation to play.", type = ArgType.STRING)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.dispatcher.get<LivingEntity>() ?: data.victim ?: return false
        val animationName = config.getFormattedString("animation", data)

        val modeledEntity = ModelEngineBridge.instance.getModeledEntity(entity) ?: return false

        var played = false

        for (model in modeledEntity.models.values) {
            val animation = model.animationHandler.getAnimation(animationName) ?: continue
            model.animationHandler.playAnimation(animation, false)
            played = true
        }

        return played
    }
}
