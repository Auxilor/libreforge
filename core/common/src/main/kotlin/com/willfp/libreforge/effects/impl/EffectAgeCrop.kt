package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.data.Ageable

object EffectAgeCrop : Effect<NoCompileData>("age_crop") {
    override val description = "Advances a crop's growth by a number of stages."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override val arguments = arguments {
        optional(
            "age",
            description = "The number of growth stages to advance. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val crop = data.block ?: data.location?.block ?: return false
        val state = crop.blockData as? Ageable ?: return false
        if (state.age == state.maximumAge) {
            return false
        }

        val age = config.getOrElse("age", 1) { getIntFromExpression(it, data) }

        val newAge = (state.age + age).coerceAtMost(state.maximumAge)
        state.age = newAge
        crop.blockData = state

        return true
    }
}
