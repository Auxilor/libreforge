package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.globalPoints
import com.willfp.libreforge.triggers.TriggerData

object EffectGiveGlobalPoints : Effect<NoCompileData>("give_global_points") {
    override val description = "Adds points to a global (server-wide) point counter when triggered."
    override val categories = setOf("economy", "points")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The global point type to add to.",
            type = ArgType.STRING
        )
        optional(
            "amount",
            description = "The amount of global points to add. Supports expressions. Defaults to 0.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "%level% * 10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        globalPoints[config.getString("type")] += config.getDoubleFromExpression("amount", data)

        return true
    }
}
