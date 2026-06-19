package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData

object EffectGiveItemPoints : Effect<NoCompileData>("give_item_points") {
    override val description = "Adds points to a specific point type on the triggering item."
    override val categories = setOf("economy", "points")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The item point type to add to.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount of points!",
            description = "The amount of points to add to the item. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val type = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", data)

        item.points[type] += amount

        return true
    }
}
