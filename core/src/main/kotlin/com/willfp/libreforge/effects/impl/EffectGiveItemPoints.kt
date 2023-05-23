package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.ItemStack

object EffectGiveItemPoints : Effect<NoCompileData>("give_item_points") {
    override val isPermanent = false

    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.holder.provider as? ItemStack ?: return false

        val type = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", data)

        item.points[type] += amount

        return true
    }
}
