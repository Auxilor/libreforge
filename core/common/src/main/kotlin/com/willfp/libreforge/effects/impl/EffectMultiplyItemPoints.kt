package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData

object EffectMultiplyItemPoints : Effect<NoCompileData>("multiply_item_points") {
    override val description = "Multiplies a point value stored on the found item by the specified amount."
    override val categories = setOf("economy", "points")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The item point type to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the item's point value. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val type = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", data)

        item.points[type] *= amount

        return true
    }
}
