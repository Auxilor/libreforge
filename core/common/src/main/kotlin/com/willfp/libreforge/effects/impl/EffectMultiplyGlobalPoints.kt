package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.globalPoints
import com.willfp.libreforge.triggers.TriggerData

object EffectMultiplyGlobalPoints : Effect<NoCompileData>("multiply_global_points") {
    override val description = "Multiplies a global point counter by the specified amount."
    override val categories = setOf("economy", "points")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The global point type to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the global point counter. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val type = config.getString("type")

        globalPoints[type] *= config.getDoubleFromExpression("multiplier", data)

        return true
    }
}
