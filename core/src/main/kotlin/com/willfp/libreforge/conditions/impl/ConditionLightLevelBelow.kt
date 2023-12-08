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

object ConditionLightLevelBelow : Condition<NoCompileData>("light_level_below") {
    override val arguments = arguments {
        require("level", "You must specify maximum light level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false

        val level = config.getIntFromExpression("level", dispatcher.get())

        return location.block.lightLevel <= level
    }
}
