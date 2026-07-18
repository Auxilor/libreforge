package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.globalPoints
import com.willfp.libreforge.triggers.TriggerData

object EffectSetGlobalPoints : Effect<NoCompileData>("set_global_points") {
    override val description = "Sets a global point counter to a specific value."
    override val categories = setOf("economy", "points")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The global point type to set.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount of points!",
            description = "The value to set the global point counter to. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        globalPoints[config.getString("type")] = config.getDoubleFromExpression("amount", data)

        return true
    }
}
