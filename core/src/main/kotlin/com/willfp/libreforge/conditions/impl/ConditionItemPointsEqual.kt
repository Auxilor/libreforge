package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.points
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionItemPointsEqual : Condition<NoCompileData>("item_points_equal") {
    override val arguments = arguments {
        require("type", "You must specify the type of points to check for!")
        require("amount", "You must specify the amount of points!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val item = holder.getProvider<ItemStack>() ?: return false
        val type = config.getString("type")
        val amount = config.getDoubleFromExpression("type", dispatcher.get<Player>())

        return item.points[type] == amount
    }
}
