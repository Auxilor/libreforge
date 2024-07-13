package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetItemPoints : Effect<NoCompileData>("set_item_points") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        item.points[config.getString("type")] = config.getDoubleFromExpression("amount", data)

        return true
    }
}
