package com.willfp.libreforge.integrations.model.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.model.ModelManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData

object EffectStopAnimation : Effect<NoCompileData>("stop_animation") {
    override val description =
        "Stops a ModelEngine or BetterModel animation on the triggering entity, or the victim if it has none."
    override val categories = setOf("visual", "entity")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "animation",
            "You must specify the animation name!",
            description = "The name of the animation to stop.",
            type = ArgType.STRING
        )
        optional(
            "fade",
            description =
                "If the animation should blend out over its lerp out time, or stop straight away. ModelEngine only.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "model",
            description =
                "Only stop the animation on the model with this ID. Leave out to stop it on every model.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val entity = data.modelTarget() ?: return false

        return ModelManager.stopAnimation(
            entity,
            config.getStringOrNull("model"),
            config.getFormattedString("animation", data),
            config.getBoolOrNull("fade") != false
        )
    }
}
